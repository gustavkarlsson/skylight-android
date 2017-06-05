package se.gustavkarlsson.skylight.android.notifications;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import javax.inject.Named;

import se.gustavkarlsson.skylight.android.cache.SingletonCache;
import se.gustavkarlsson.skylight.android.evaluation.Chance;
import se.gustavkarlsson.skylight.android.evaluation.ChanceEvaluator;
import se.gustavkarlsson.skylight.android.evaluation.ChanceLevel;
import se.gustavkarlsson.skylight.android.models.AuroraReport;
import se.gustavkarlsson.skylight.android.settings.Settings;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static se.gustavkarlsson.skylight.android.dagger.Names.LAST_NOTIFIED_NAME;

@RunWith(MockitoJUnitRunner.class)
public class NotificationDeciderTest {

	@Mock
	@Named(LAST_NOTIFIED_NAME)
	SingletonCache<AuroraReport> lastNotifiedCache;

	@Mock
	ChanceEvaluator<AuroraReport> auroraChanceEvaluator;

	@Mock
	Settings settings;

	@Mock
	ReportOutdatedEvaluator reportOutdatedEvaluator;

	private NotificationDecider notificationDecider;
	private AuroraReport report;
	private AuroraReport lastReport;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		notificationDecider = new NotificationDecider(lastNotifiedCache, auroraChanceEvaluator, settings, reportOutdatedEvaluator);
		report = createDummyReport();
		lastReport = createDummyReport();
		when(settings.isEnableNotifications()).thenReturn(true);
		when(settings.getTriggerLevel()).thenReturn(ChanceLevel.HIGH);
		when(auroraChanceEvaluator.evaluate(any())).thenReturn(Chance.Companion.of(1));
		when(reportOutdatedEvaluator.isOutdated(any())).thenReturn(true);
	}

	private static AuroraReport createDummyReport() {
		return new AuroraReport(0, null, null);
	}

	@Test
	public void maxChanceShouldNotify() throws Exception {
		when(settings.getTriggerLevel()).thenReturn(ChanceLevel.HIGH);

		boolean shouldNotify = notificationDecider.shouldNotify(report);

		assertThat(shouldNotify).isTrue();
	}

	@Test
	public void mediumChanceShouldNotifyIfTriggerLevelIsLow() throws Exception {
		when(settings.getTriggerLevel()).thenReturn(ChanceLevel.LOW);
		when(auroraChanceEvaluator.evaluate(report)).thenReturn(Chance.Companion.of(0.5));

		boolean shouldNotify = notificationDecider.shouldNotify(report);

		assertThat(shouldNotify).isTrue();
	}

	@Test
	public void mediumChanceShouldNotNotifyIfTriggerLevelIsHigh() throws Exception {
		when(settings.getTriggerLevel()).thenReturn(ChanceLevel.HIGH);
		when(auroraChanceEvaluator.evaluate(report)).thenReturn(Chance.Companion.of(0.5));

		boolean shouldNotify = notificationDecider.shouldNotify(report);

		assertThat(shouldNotify).isFalse();
	}

	@Test
	public void noChanceShouldNotNotify() throws Exception {
		when(auroraChanceEvaluator.evaluate(report)).thenReturn(Chance.Companion.of(0));

		boolean shouldNotify = notificationDecider.shouldNotify(report);

		assertThat(shouldNotify).isFalse();
	}

	@Test
	public void notificationsTurnedOffChanceShouldNotNotify() throws Exception {
		when(settings.isEnableNotifications()).thenReturn(false);

		boolean shouldNotify = notificationDecider.shouldNotify(report);

		assertThat(shouldNotify).isFalse();
	}

	@Test
	public void alreadyNotifiedAtSameLevelShouldNotNotify() throws Exception {
		when(lastNotifiedCache.getValue()).thenReturn(lastReport);
		when(reportOutdatedEvaluator.isOutdated(lastReport)).thenReturn(false);

		boolean shouldNotify = notificationDecider.shouldNotify(report);

		assertThat(shouldNotify).isFalse();
	}

	@Test
	public void alreadyNotifiedAtLowerLevelShouldNotify() throws Exception {
		when(lastNotifiedCache.getValue()).thenReturn(lastReport);
		when(reportOutdatedEvaluator.isOutdated(lastReport)).thenReturn(false);
		when(auroraChanceEvaluator.evaluate(lastReport)).thenReturn(Chance.Companion.of(0.5));

		boolean shouldNotify = notificationDecider.shouldNotify(report);

		assertThat(shouldNotify).isTrue();
	}
}

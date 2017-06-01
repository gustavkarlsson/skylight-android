package se.gustavkarlsson.skylight.android.notifications;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import se.gustavkarlsson.skylight.android.cache.ReportNotificationCache;
import se.gustavkarlsson.skylight.android.evaluation.Chance;
import se.gustavkarlsson.skylight.android.evaluation.ChanceEvaluator;
import se.gustavkarlsson.skylight.android.evaluation.PresentableChance;
import se.gustavkarlsson.skylight.android.models.AuroraReport;
import se.gustavkarlsson.skylight.android.settings.Settings;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class NotificationDeciderTest {

	@Mock
	ReportNotificationCache cache;

	@Mock
	ChanceEvaluator<AuroraReport> chanceEvaluator;

	@Mock
	Settings settings;

	@Mock
	ReportOutdatedEvaluator outdatedEvaluator;

	private NotificationDecider decider;
	private AuroraReport report;
	private AuroraReport lastReport;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		decider = new NotificationDecider(cache, chanceEvaluator, settings, outdatedEvaluator);
		report = createDummyReport();
		lastReport = createDummyReport();
		when(settings.isEnableNotifications()).thenReturn(true);
		when(settings.getTriggerLevel()).thenReturn(PresentableChance.HIGH);
		when(chanceEvaluator.evaluate(any())).thenReturn(Chance.of(1));
		when(outdatedEvaluator.isOutdated(any())).thenReturn(true);
	}

	private static AuroraReport createDummyReport() {
		return new AuroraReport(0, null, null);
	}

	@Test
	public void maxChanceShouldNotify() throws Exception {
		when(settings.getTriggerLevel()).thenReturn(PresentableChance.HIGH);

		boolean shouldNotify = decider.shouldNotify(report);

		assertThat(shouldNotify).isTrue();
	}

	@Test
	public void mediumChanceShouldNotifyIfTriggerLevelIsLow() throws Exception {
		when(settings.getTriggerLevel()).thenReturn(PresentableChance.LOW);
		when(chanceEvaluator.evaluate(report)).thenReturn(Chance.of(0.5));

		boolean shouldNotify = decider.shouldNotify(report);

		assertThat(shouldNotify).isTrue();
	}

	@Test
	public void mediumChanceShouldNotNotifyIfTriggerLevelIsHigh() throws Exception {
		when(settings.getTriggerLevel()).thenReturn(PresentableChance.HIGH);
		when(chanceEvaluator.evaluate(report)).thenReturn(Chance.of(0.5));

		boolean shouldNotify = decider.shouldNotify(report);

		assertThat(shouldNotify).isFalse();
	}

	@Test
	public void noChanceShouldNotNotify() throws Exception {
		when(chanceEvaluator.evaluate(report)).thenReturn(Chance.of(0));

		boolean shouldNotify = decider.shouldNotify(report);

		assertThat(shouldNotify).isFalse();
	}

	@Test
	public void notificationsTurnedOffChanceShouldNotNotify() throws Exception {
		when(settings.isEnableNotifications()).thenReturn(false);

		boolean shouldNotify = decider.shouldNotify(report);

		assertThat(shouldNotify).isFalse();
	}

	@Test
	public void alreadyNotifiedAtSameLevelShouldNotNotify() throws Exception {
		when(cache.getLastNotified()).thenReturn(lastReport);
		when(outdatedEvaluator.isOutdated(lastReport)).thenReturn(false);

		boolean shouldNotify = decider.shouldNotify(report);

		assertThat(shouldNotify).isFalse();
	}

	@Test
	public void alreadyNotifiedAtLowerLevelShouldNotify() throws Exception {
		when(cache.getLastNotified()).thenReturn(lastReport);
		when(outdatedEvaluator.isOutdated(lastReport)).thenReturn(false);
		when(chanceEvaluator.evaluate(lastReport)).thenReturn(Chance.of(0.5));

		boolean shouldNotify = decider.shouldNotify(report);

		assertThat(shouldNotify).isTrue();
	}
}

package se.gustavkarlsson.skylight.android.notifications;

import org.assertj.core.api.AbstractBooleanAssert;
import org.assertj.core.api.SoftAssertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.threeten.bp.Clock;
import org.threeten.bp.Instant;
import org.threeten.bp.ZoneId;
import org.threeten.bp.ZoneOffset;

import java8.util.function.Supplier;
import se.gustavkarlsson.skylight.android.models.AuroraReport;

import static org.mockito.Mockito.when;
import static org.threeten.bp.temporal.ChronoUnit.DAYS;
import static org.threeten.bp.temporal.ChronoUnit.HOURS;

@RunWith(MockitoJUnitRunner.class)
public class ReportOutdatedEvaluatorTest {
	private static final ZoneOffset ZONE_OFFSET = ZoneOffset.UTC;
	private static final Instant MIDNIGHT = Instant.EPOCH;
	private static final Instant BEFORE_MIDNIGHT = MIDNIGHT.minusSeconds(1);
	private static final Instant AFTER_MIDNIGHT = MIDNIGHT.plusSeconds(1);
	private static final Instant NOON = MIDNIGHT.plus(12, HOURS);
	private static final Instant AFTER_NOON = NOON.plusSeconds(1);
	private static final Instant BEFORE_NOON = NOON.minusSeconds(1);

	@Mock
	Clock clock;

	@Mock
	Supplier<ZoneId> zoneIdSupplier;

	@Mock
	AuroraReport report;

	private ReportOutdatedEvaluator evaluator;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		when(zoneIdSupplier.get()).thenReturn(ZONE_OFFSET);
		when(clock.getZone()).thenReturn(ZONE_OFFSET);
		evaluator = new ReportOutdatedEvaluator(clock, zoneIdSupplier);
	}

	@Test
	public void testMultiple() throws Exception {
		SoftAssertions softly = new SoftAssertions();

		assertOutdated(BEFORE_MIDNIGHT, AFTER_MIDNIGHT, false, softly);
		assertOutdated(BEFORE_MIDNIGHT, MIDNIGHT, false, softly);
		assertOutdated(MIDNIGHT, AFTER_MIDNIGHT, false, softly);

		assertOutdated(BEFORE_MIDNIGHT, BEFORE_NOON, false, softly);
		assertOutdated(AFTER_MIDNIGHT, BEFORE_NOON, false, softly);

		assertOutdated(BEFORE_MIDNIGHT, AFTER_NOON, true, softly);
		assertOutdated(AFTER_MIDNIGHT, AFTER_NOON, true, softly);

		assertOutdated(BEFORE_NOON, AFTER_NOON, true, softly);

		assertOutdated(BEFORE_NOON.minus(1, DAYS), BEFORE_NOON, true, softly);
		assertOutdated(BEFORE_NOON.minus(1, DAYS), AFTER_NOON, true, softly);

		assertOutdated(AFTER_NOON.minus(1, DAYS), BEFORE_NOON, true, softly);
		assertOutdated(AFTER_NOON.minus(1, DAYS), AFTER_NOON, true, softly);

		softly.assertAll();
	}

	private void assertOutdated(Instant lastReportTime, Instant currentTime, boolean expected, SoftAssertions softly) {
		when(report.getTimestampMillis()).thenReturn(lastReportTime.toEpochMilli());
		when(clock.instant()).thenReturn(currentTime);
		when(clock.millis()).thenReturn(currentTime.toEpochMilli());

		boolean outdated = evaluator.isOutdated(report);

		AbstractBooleanAssert<?> assertion = softly.assertThat(outdated).as("Last report time: %s, Current time: %s", lastReportTime, currentTime);
		if (expected) {
			assertion.isTrue();
		} else {
			assertion.isFalse();
		}
	}

}

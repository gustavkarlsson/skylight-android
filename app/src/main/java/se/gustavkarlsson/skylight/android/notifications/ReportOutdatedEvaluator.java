package se.gustavkarlsson.skylight.android.notifications;

import org.threeten.bp.Clock;
import org.threeten.bp.Instant;
import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalTime;
import org.threeten.bp.ZoneId;
import org.threeten.bp.ZoneOffset;

import javax.inject.Inject;

import se.gustavkarlsson.skylight.android.models.AuroraReport;

public class ReportOutdatedEvaluator {

	private final Clock clock;
	private final LocalTime delimiterTime;

	@Inject
	public ReportOutdatedEvaluator(LocalTime delimiterTime) {
		this(Clock.systemUTC(), delimiterTime);
	}

	ReportOutdatedEvaluator(Clock clock, LocalTime delimiterTime) {
		this.clock = clock;
		this.delimiterTime = delimiterTime;
	}


	boolean isOutdated(AuroraReport report) {
		ZoneId currentZoneId = ZoneOffset.systemDefault();
		LocalDate today = LocalDate.now(clock);
		Instant noonToday = delimiterTime.atDate(today).atZone(currentZoneId).toInstant();
		Instant reportTime = Instant.ofEpochMilli(report.getTimestampMillis());
		return reportTime.isBefore(noonToday);
	}
}

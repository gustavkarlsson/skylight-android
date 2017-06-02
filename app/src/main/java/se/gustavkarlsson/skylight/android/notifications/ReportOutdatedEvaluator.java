package se.gustavkarlsson.skylight.android.notifications;

import org.threeten.bp.Clock;
import org.threeten.bp.Duration;
import org.threeten.bp.Instant;
import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalTime;
import org.threeten.bp.ZoneId;

import javax.inject.Inject;

import dagger.Reusable;
import java8.util.function.Supplier;
import se.gustavkarlsson.skylight.android.models.AuroraReport;

@Reusable
public class ReportOutdatedEvaluator {

	private final Clock clock;
	private final Supplier<ZoneId> zoneIdSupplier;

	@Inject
	ReportOutdatedEvaluator(Clock clock, Supplier<ZoneId> zoneIdSupplier) {
		this.clock = clock;
		this.zoneIdSupplier = zoneIdSupplier;
	}


	boolean isOutdated(AuroraReport report) {
		ZoneId currentZoneId = zoneIdSupplier.get();
		Instant now = Instant.now(clock);
		LocalDate today = LocalDate.now(clock);
		Instant noonToday = LocalTime.NOON.atDate(today).atZone(currentZoneId).toInstant();
		Instant reportTime = Instant.ofEpochMilli(report.getTimestampMillis());
		Duration duration = Duration.between(reportTime, now);
		return duration.toHours() > 12 || (now.isAfter(noonToday) && reportTime.isBefore(noonToday));
	}
}

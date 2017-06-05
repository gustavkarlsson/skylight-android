package se.gustavkarlsson.skylight.android.notifications

import dagger.Reusable
import java8.util.function.Supplier
import org.threeten.bp.*
import se.gustavkarlsson.skylight.android.models.AuroraReport
import javax.inject.Inject

@Reusable
class ReportOutdatedEvaluator
@Inject
internal constructor(
		private val clock: Clock,
		private val zoneIdSupplier: Supplier<ZoneId>
) {

	fun isOutdated(report: AuroraReport): Boolean {
		val currentZoneId = zoneIdSupplier.get()
		val now = Instant.now(clock)
		val today = LocalDate.now(clock)
		val noonToday = LocalTime.NOON.atDate(today).atZone(currentZoneId).toInstant()
		val reportTime = Instant.ofEpochMilli(report.timestampMillis)
		val duration = Duration.between(reportTime, now)
		return duration.toHours() > 12 || now.isAfter(noonToday) && reportTime.isBefore(noonToday)
	}
}

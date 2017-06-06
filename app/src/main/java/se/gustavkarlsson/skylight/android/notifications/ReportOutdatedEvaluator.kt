package se.gustavkarlsson.skylight.android.notifications

import dagger.Reusable
import java8.util.function.Supplier
import org.threeten.bp.Clock
import org.threeten.bp.Instant
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime.NOON
import org.threeten.bp.ZoneId
import se.gustavkarlsson.skylight.android.extensions.until
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
		val noonToday = NOON.atDate(today).atZone(currentZoneId).toInstant()
		val reportTime = Instant.ofEpochMilli(report.timestampMillis)
		val duration = reportTime.until(now)
		return duration.toHours() > 12 || now.isAfter(noonToday) && reportTime.isBefore(noonToday)
	}
}

package se.gustavkarlsson.skylight.android.notifications

import dagger.Reusable
import org.threeten.bp.Clock
import org.threeten.bp.Instant
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime.NOON
import se.gustavkarlsson.skylight.android.extensions.until
import se.gustavkarlsson.skylight.android.models.AuroraReport
import se.gustavkarlsson.skylight.android.util.ZoneIdProvider
import javax.inject.Inject

@Reusable
class ReportOutdatedEvaluator
@Inject
constructor(
		private val clock: Clock,
		private val zoneIdProvider: ZoneIdProvider
) {

	fun isOutdated(report: AuroraReport): Boolean {
		val currentZoneId = zoneIdProvider.zoneId
		val now = Instant.now(clock)
		val today = LocalDate.now(clock)
		val noonToday = NOON.atDate(today).atZone(currentZoneId).toInstant()
		val reportTime = report.timestamp
		val duration = reportTime until now
		// TODO replace with arithmetic
		return duration.toHours() > 12 || now.isAfter(noonToday) && reportTime.isBefore(noonToday)
	}
}

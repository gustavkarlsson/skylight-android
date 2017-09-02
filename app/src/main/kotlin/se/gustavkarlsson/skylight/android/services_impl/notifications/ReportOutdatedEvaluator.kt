package se.gustavkarlsson.skylight.android.services_impl.notifications

import dagger.Reusable
import org.threeten.bp.Clock
import org.threeten.bp.LocalTime.NOON
import se.gustavkarlsson.skylight.android.entities.AuroraReport
import se.gustavkarlsson.skylight.android.extensions.now
import se.gustavkarlsson.skylight.android.extensions.today
import se.gustavkarlsson.skylight.android.extensions.until
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
		val now = clock.now
		val today = clock.today
		val noonToday = NOON.atDate(today).atZone(currentZoneId).toInstant()
		val reportTime = report.timestamp
		val reportAge = reportTime until now
		return reportAge.toHours() > 12 || now > noonToday && reportTime < noonToday
	}
}

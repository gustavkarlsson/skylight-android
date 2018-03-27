package se.gustavkarlsson.skylight.android.services_impl.notifications

import dagger.Reusable
import org.threeten.bp.LocalTime.NOON
import se.gustavkarlsson.skylight.android.entities.AuroraReport
import se.gustavkarlsson.skylight.android.extensions.until
import se.gustavkarlsson.skylight.android.services.providers.TimeProvider
import javax.inject.Inject

@Reusable
class ReportOutdatedEvaluator
@Inject
constructor(
	private val timeProvider: TimeProvider
) {

	fun isOutdated(report: AuroraReport): Boolean {
		val currentZoneId = timeProvider.getZoneId().blockingGet()
		val now = timeProvider.getTime().blockingGet()
		val today = timeProvider.getLocalDate().blockingGet()
		val noonToday = NOON.atDate(today).atZone(currentZoneId).toInstant()
		val reportTime = report.timestamp
		val reportAge = reportTime until now
		return reportAge.toHours() > 12 || now > noonToday && reportTime < noonToday
	}
}

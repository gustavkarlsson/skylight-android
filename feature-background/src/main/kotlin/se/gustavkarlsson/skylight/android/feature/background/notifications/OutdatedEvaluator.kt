package se.gustavkarlsson.skylight.android.feature.background.notifications

import org.threeten.bp.Instant
import org.threeten.bp.LocalTime.NOON
import se.gustavkarlsson.skylight.android.extensions.until
import se.gustavkarlsson.skylight.android.services.Time

internal class OutdatedEvaluator(
	private val time: Time
) {

	fun isOutdated(time: Instant): Boolean {
		val currentZoneId = this.time.zoneId().blockingGet()
		val now = this.time.now().blockingGet()
		val today = this.time.localDate().blockingGet()
		val noonToday = NOON.atDate(today).atZone(currentZoneId).toInstant()
		val age = time until now
		return age.toHours() > 12 || now > noonToday && time < noonToday
	}
}

package se.gustavkarlsson.skylight.android.services_impl.notifications

import dagger.Reusable
import org.threeten.bp.Instant
import org.threeten.bp.LocalTime.NOON
import se.gustavkarlsson.skylight.android.extensions.until
import se.gustavkarlsson.skylight.android.services.providers.TimeProvider
import javax.inject.Inject

@Reusable
class OutdatedEvaluator
@Inject
constructor(
	private val timeProvider: TimeProvider
) {

	fun isOutdated(time: Instant): Boolean {
		val currentZoneId = timeProvider.getZoneId().blockingGet()
		val now = timeProvider.getTime().blockingGet()
		val today = timeProvider.getLocalDate().blockingGet()
		val noonToday = NOON.atDate(today).atZone(currentZoneId).toInstant()
		val age = time until now
		return age.toHours() > 12 || now > noonToday && time < noonToday
	}
}

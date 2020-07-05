package se.gustavkarlsson.skylight.android.feature.background.notifications

import org.threeten.bp.Instant
import org.threeten.bp.LocalTime.NOON
import se.gustavkarlsson.skylight.android.lib.time.Time
import se.gustavkarlsson.skylight.android.utils.until

internal class OutdatedEvaluator(
    private val time: Time
) {

    fun isOutdated(time: Instant): Boolean {
        val currentZoneId = this.time.zoneId()
        val now = this.time.now()
        val today = now.atZone(currentZoneId).toLocalDate()
        val noonToday = NOON.atDate(today).atZone(currentZoneId).toInstant()
        val age = time until now
        return age.toHours() > 12 || now > noonToday && time < noonToday
    }
}

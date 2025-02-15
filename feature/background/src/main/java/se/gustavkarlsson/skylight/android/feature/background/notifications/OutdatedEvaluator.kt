package se.gustavkarlsson.skylight.android.feature.background.notifications

import kotlinx.datetime.Instant
import kotlinx.datetime.atTime
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import me.tatarka.inject.annotations.Inject
import se.gustavkarlsson.skylight.android.lib.time.Time

@Inject
internal class OutdatedEvaluator(
    private val time: Time,
) {

    fun isOutdated(time: Instant): Boolean {
        val currentTimeZone = this.time.timeZone()
        val now = this.time.now()
        val today = now.toLocalDateTime(currentTimeZone).date
        val noonToday = today.atTime(12, 0, 0).toInstant(currentTimeZone)
        val age = now - time
        return age.inWholeHours > 12 || now > noonToday && time < noonToday
    }
}

package se.gustavkarlsson.skylight.android.feature.main

import android.text.format.DateUtils
import org.threeten.bp.Duration
import org.threeten.bp.Instant
import se.gustavkarlsson.skylight.android.core.utils.until

internal class DateUtilsRelativeTimeFormatter(
    private val rightNowText: CharSequence
) : RelativeTimeFormatter {
    override fun format(time: Instant, now: Instant, minResolution: Duration): CharSequence {
        require(!minResolution.isNegative) { "minResolution is negative: $minResolution" }
        return when {
            time.isCloseTo(now, minResolution) -> {
                rightNowText
            }
            else -> {
                val timeMillis = time.toEpochMilli()
                val nowMillis = now.toEpochMilli()
                val minResolutionMillis = minResolution.toMillis()
                DateUtils.getRelativeTimeSpanString(timeMillis, nowMillis, minResolutionMillis)
            }
        }
    }

    private fun Instant.isCloseTo(other: Instant, threshold: Duration): Boolean {
        val age = this until other
        return age.abs() < threshold
    }
}

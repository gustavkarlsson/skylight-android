package se.gustavkarlsson.skylight.android.feature.main.util

import android.text.format.DateUtils
import com.ioki.textref.TextRef
import se.gustavkarlsson.skylight.android.feature.main.R
import kotlin.time.Duration
import kotlin.time.Instant

internal interface RelativeTimeFormatter {
    fun format(time: Instant, now: Instant, minResolution: Duration): TextRef
}

internal object DateUtilsRelativeTimeFormatter : RelativeTimeFormatter {
    override fun format(time: Instant, now: Instant, minResolution: Duration): TextRef {
        require(!minResolution.isNegative()) { "minResolution is negative: $minResolution" }
        return when {
            time.isCloseTo(now, minResolution) -> {
                TextRef.stringRes(R.string.right_now)
            }

            else -> {
                val timeMillis = time.toEpochMilliseconds()
                val nowMillis = now.toEpochMilliseconds()
                val minResolutionMillis = minResolution.inWholeMilliseconds
                val s = DateUtils.getRelativeTimeSpanString(timeMillis, nowMillis, minResolutionMillis)
                TextRef.string(s.toString())
            }
        }
    }

    private fun Instant.isCloseTo(other: Instant, threshold: Duration): Boolean {
        val age = other - this
        return age.absoluteValue < threshold
    }
}

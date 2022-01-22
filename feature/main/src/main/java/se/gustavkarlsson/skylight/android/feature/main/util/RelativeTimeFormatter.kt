package se.gustavkarlsson.skylight.android.feature.main.util

import android.text.format.DateUtils
import com.ioki.textref.TextRef
import com.squareup.anvil.annotations.ContributesTo
import dagger.Module
import dagger.Provides
import org.threeten.bp.Duration
import org.threeten.bp.Instant
import se.gustavkarlsson.skylight.android.core.utils.until
import se.gustavkarlsson.skylight.android.feature.main.R
import se.gustavkarlsson.skylight.android.feature.main.viewmodel.MainScopeMarker

@Module
@ContributesTo(MainScopeMarker::class)
object RelativeTimeFormatterModule {
    @Provides
    internal fun relativeTimeFormatter(): RelativeTimeFormatter = DateUtilsRelativeTimeFormatter
}

internal interface RelativeTimeFormatter {
    fun format(time: Instant, now: Instant, minResolution: Duration): TextRef
}

internal object DateUtilsRelativeTimeFormatter : RelativeTimeFormatter {
    override fun format(time: Instant, now: Instant, minResolution: Duration): TextRef {
        require(!minResolution.isNegative) { "minResolution is negative: $minResolution" }
        return when {
            time.isCloseTo(now, minResolution) -> {
                TextRef.stringRes(R.string.right_now)
            }
            else -> {
                val timeMillis = time.toEpochMilli()
                val nowMillis = now.toEpochMilli()
                val minResolutionMillis = minResolution.toMillis()
                val s = DateUtils.getRelativeTimeSpanString(timeMillis, nowMillis, minResolutionMillis)
                TextRef.string(s.toString())
            }
        }
    }

    private fun Instant.isCloseTo(other: Instant, threshold: Duration): Boolean {
        val age = this until other
        return age.abs() < threshold
    }
}

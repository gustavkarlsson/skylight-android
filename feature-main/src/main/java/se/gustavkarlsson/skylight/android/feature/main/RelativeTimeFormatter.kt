package se.gustavkarlsson.skylight.android.feature.main

import org.threeten.bp.Duration
import org.threeten.bp.Instant

internal interface RelativeTimeFormatter {
	fun format(time: Instant, now: Instant, minResolution: Duration): CharSequence
}

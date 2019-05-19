package se.gustavkarlsson.skylight.android.formatters

import org.threeten.bp.Duration
import org.threeten.bp.Instant

interface RelativeTimeFormatter {
	fun format(time: Instant, now: Instant, minResolution: Duration): CharSequence
}

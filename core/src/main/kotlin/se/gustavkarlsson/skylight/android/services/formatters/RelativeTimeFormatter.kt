package se.gustavkarlsson.skylight.android.services.formatters

import org.threeten.bp.Duration
import org.threeten.bp.Instant

interface RelativeTimeFormatter {
	fun format(time: Instant, now: Instant, minResolution: Duration): CharSequence
}

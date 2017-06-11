package se.gustavkarlsson.skylight.android.extensions

import org.threeten.bp.Clock
import org.threeten.bp.Duration
import org.threeten.bp.Instant
import org.threeten.bp.temporal.Temporal

infix fun Temporal.until(other: Temporal): Duration = Duration.between(this, other)

val Clock.now: Instant
	get() = this.instant()

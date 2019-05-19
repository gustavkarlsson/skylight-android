package se.gustavkarlsson.skylight.android.extensions

import org.threeten.bp.Duration
import org.threeten.bp.Instant
import org.threeten.bp.temporal.Temporal
import java.util.GregorianCalendar

infix fun Temporal.until(other: Temporal): Duration = Duration.between(this, other)

fun Instant.toGregorianCalendar() = GregorianCalendar().apply { timeInMillis = toEpochMilli() }

inline val Long.millis: Duration
	get() = Duration.ofMillis(this)

inline val Long.seconds: Duration
	get() = Duration.ofSeconds(this)

inline val Long.minutes: Duration
	get() = Duration.ofMinutes(this)

inline val Int.millis: Duration
	get() = this.toLong().millis

inline val Int.seconds: Duration
	get() = this.toLong().seconds

inline val Int.minutes: Duration
	get() = this.toLong().minutes

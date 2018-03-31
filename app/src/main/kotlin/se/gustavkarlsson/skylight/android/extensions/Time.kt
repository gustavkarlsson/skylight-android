package se.gustavkarlsson.skylight.android.extensions

import org.threeten.bp.Duration
import org.threeten.bp.Instant
import org.threeten.bp.temporal.Temporal
import java.util.*

infix fun Temporal.until(other: Temporal): Duration = Duration.between(this, other)

fun Instant.toGregorianCalendar() = GregorianCalendar().apply { timeInMillis = toEpochMilli() }

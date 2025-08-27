package se.gustavkarlsson.skylight.android.lib.time

import kotlinx.datetime.TimeZone
import kotlin.time.Instant

interface Time {
    fun now(): Instant
    fun timeZone(): TimeZone
}

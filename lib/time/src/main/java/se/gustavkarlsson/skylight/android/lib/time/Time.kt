package se.gustavkarlsson.skylight.android.lib.time

import kotlin.time.Instant
import kotlinx.datetime.TimeZone

interface Time {
    fun now(): Instant
    fun timeZone(): TimeZone
}

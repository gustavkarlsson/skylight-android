package se.gustavkarlsson.skylight.android.lib.time

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone

internal object SystemTime : Time {
    override fun now(): Instant = Clock.System.now()
    override fun timeZone(): TimeZone = TimeZone.currentSystemDefault()
}

package se.gustavkarlsson.skylight.android.lib.time

import kotlin.time.Clock
import kotlin.time.Instant
import kotlinx.datetime.TimeZone

internal object SystemTime : Time {
    override fun now(): Instant = Clock.System.now()
    override fun timeZone(): TimeZone = TimeZone.currentSystemDefault()
}

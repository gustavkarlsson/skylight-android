package se.gustavkarlsson.skylight.android.lib.time

import kotlinx.datetime.TimeZone
import kotlin.time.Clock
import kotlin.time.Instant

internal object SystemTime : Time {
    override fun now(): Instant = Clock.System.now()
    override fun timeZone(): TimeZone = TimeZone.currentSystemDefault()
}

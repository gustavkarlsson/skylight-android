package se.gustavkarlsson.skylight.android.lib.time

import org.threeten.bp.Instant
import org.threeten.bp.ZoneId

internal object SystemTime : Time {
    override fun now(): Instant = Instant.now()
    override fun zoneId(): ZoneId = ZoneId.systemDefault()
}

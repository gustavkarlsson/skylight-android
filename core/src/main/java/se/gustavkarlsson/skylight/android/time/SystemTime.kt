package se.gustavkarlsson.skylight.android.time

import org.threeten.bp.Instant
import org.threeten.bp.ZoneId
import se.gustavkarlsson.skylight.android.services.Time

internal object SystemTime : Time {
    override fun now(): Instant = Instant.now()
    override fun zoneId(): ZoneId = ZoneId.systemDefault()
}

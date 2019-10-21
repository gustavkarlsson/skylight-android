package se.gustavkarlsson.skylight.android.time

import org.threeten.bp.Instant
import org.threeten.bp.ZoneId
import se.gustavkarlsson.skylight.android.services.Time

internal class SystemTimeWithFixedZoneId(private val zoneId: ZoneId) : Time {
	override fun now(): Instant = Instant.now()
	override fun zoneId(): ZoneId = zoneId
}

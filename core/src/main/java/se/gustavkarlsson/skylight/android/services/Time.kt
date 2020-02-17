package se.gustavkarlsson.skylight.android.services

import org.threeten.bp.Instant
import org.threeten.bp.ZoneId

interface Time {
    fun now(): Instant
    fun zoneId(): ZoneId
}

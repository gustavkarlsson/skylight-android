package se.gustavkarlsson.skylight.android.feature.background.persistence

import se.gustavkarlsson.skylight.android.core.entities.ChanceLevel
import se.gustavkarlsson.skylight.android.lib.places.PlaceId
import kotlin.time.Instant

internal data class NotificationRecord(
    val data: Set<PlaceIdWithChance>,
    val timestamp: Instant,
)

internal data class PlaceIdWithChance(val id: PlaceId, val chanceLevel: ChanceLevel)

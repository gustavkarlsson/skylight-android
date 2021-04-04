package se.gustavkarlsson.skylight.android.feature.background.persistence

import org.threeten.bp.Instant
import se.gustavkarlsson.skylight.android.core.entities.ChanceLevel

internal data class NotificationRecord(
    val data: Set<PlaceRefWithChance>,
    val timestamp: Instant
)

internal data class PlaceRefWithChance(val placeRef: PlaceRef, val chanceLevel: ChanceLevel)

internal sealed class PlaceRef {
    object Current : PlaceRef()
    data class Saved(val id: Long) : PlaceRef()
}

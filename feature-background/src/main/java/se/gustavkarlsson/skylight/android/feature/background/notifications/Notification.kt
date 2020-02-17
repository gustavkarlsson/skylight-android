package se.gustavkarlsson.skylight.android.feature.background.notifications

import org.threeten.bp.Instant
import se.gustavkarlsson.skylight.android.entities.ChanceLevel
import se.gustavkarlsson.skylight.android.entities.Place

internal data class Notification(
    val data: List<PlaceWithChance>,
    val timestamp: Instant
)

internal data class PlaceWithChance(val place: Place, val chanceLevel: ChanceLevel)

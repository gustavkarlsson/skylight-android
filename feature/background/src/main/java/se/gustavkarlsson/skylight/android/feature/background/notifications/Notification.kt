package se.gustavkarlsson.skylight.android.feature.background.notifications

import org.threeten.bp.Instant
import se.gustavkarlsson.skylight.android.core.entities.ChanceLevel
import se.gustavkarlsson.skylight.android.lib.places.Place

internal data class Notification(
    val placeToOpen: Place,
    val placesWithChance: List<PlaceWithChance>,
    val timestamp: Instant,
)

internal data class PlaceWithChance(val place: Place, val chanceLevel: ChanceLevel)

package se.gustavkarlsson.skylight.android.feature.background.notifications

import arrow.core.NonEmptyList
import org.threeten.bp.Instant
import se.gustavkarlsson.skylight.android.core.entities.ChanceLevel
import se.gustavkarlsson.skylight.android.lib.places.Place

internal data class Notification(
    val placesWithChance: NonEmptyList<PlaceWithChance>,
    val timestamp: Instant,
)

internal data class PlaceWithChance(val place: Place, val chanceLevel: ChanceLevel)

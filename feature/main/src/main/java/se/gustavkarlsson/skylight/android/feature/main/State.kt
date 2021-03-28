package se.gustavkarlsson.skylight.android.feature.main

import com.ioki.textref.TextRef
import se.gustavkarlsson.skylight.android.core.entities.TriggerLevel
import se.gustavkarlsson.skylight.android.lib.aurora.LoadableAuroraReport
import se.gustavkarlsson.skylight.android.lib.geocoder.PlaceSuggestion
import se.gustavkarlsson.skylight.android.lib.permissions.Access
import se.gustavkarlsson.skylight.android.lib.places.Place

internal data class State(
    val locationAccess: Access,
    val selectedPlaceId: Long?,
    val selectedAuroraReport: LoadableAuroraReport,
    val search: Search,
    val places: List<Place>,
    val notificationTriggerLevels: Map<Long?, TriggerLevel>,
) {
    val selectedPlace: Place
        get() = places.firstOrNull { place ->
            place.id == selectedPlaceId
        } ?: Place.Current

    val selectedPlaceTriggerLevel: TriggerLevel
        get() = notificationTriggerLevels[selectedPlaceId] ?: TriggerLevel.NEVER
}

// FIXME handle errors
internal sealed class Search {
    object Closed : Search()
    data class Open(val query: String, val suggestions: Suggestions, val error: TextRef?) : Search()
}

internal data class Suggestions(val query: String, val items: List<PlaceSuggestion>)

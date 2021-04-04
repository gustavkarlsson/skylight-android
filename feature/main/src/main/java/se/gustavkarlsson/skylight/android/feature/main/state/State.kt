package se.gustavkarlsson.skylight.android.feature.main.state

import com.ioki.textref.TextRef
import se.gustavkarlsson.skylight.android.core.entities.TriggerLevel
import se.gustavkarlsson.skylight.android.lib.aurora.LoadableAuroraReport
import se.gustavkarlsson.skylight.android.lib.geocoder.PlaceSuggestion
import se.gustavkarlsson.skylight.android.lib.permissions.Access
import se.gustavkarlsson.skylight.android.lib.places.Place
import se.gustavkarlsson.skylight.android.lib.places.PlaceId

internal data class State(
    val locationAccess: Access,
    val selectedPlaceId: PlaceId,
    val selectedAuroraReport: LoadableAuroraReport,
    val search: Search,
    val places: List<Place>,
    val notificationTriggerLevels: Map<PlaceId, TriggerLevel>,
) {
    val selectedPlace: Place
        get() = places.firstOrNull { place ->
            place.id == selectedPlaceId
        } ?: Place.Current

    val selectedPlaceTriggerLevel: TriggerLevel
        get() = notificationTriggerLevels[selectedPlaceId] ?: TriggerLevel.NEVER
}

internal sealed class Search {
    object Inactive : Search()
    sealed class Active : Search() {
        abstract val query: String
        abstract val suggestions: Suggestions

        data class Blank(
            override val query: String,
        ) : Active() {
            override val suggestions = Suggestions(query = "", items = emptyList())
        }

        data class Filled(
            override val query: String,
            override val suggestions: Suggestions,
        ) : Active()

        data class Error(
            override val query: String,
            val errorQuery: String,
            val text: TextRef,
        ) : Active() {
            override val suggestions: Suggestions get() = Suggestions(query = errorQuery, items = emptyList())
        }
    }
}

internal data class Suggestions(val query: String, val items: List<PlaceSuggestion>)

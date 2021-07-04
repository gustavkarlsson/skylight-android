package se.gustavkarlsson.skylight.android.feature.main.state

import com.ioki.textref.TextRef
import se.gustavkarlsson.skylight.android.core.entities.Loadable
import se.gustavkarlsson.skylight.android.core.entities.TriggerLevel
import se.gustavkarlsson.skylight.android.lib.aurora.LoadableAuroraReport
import se.gustavkarlsson.skylight.android.lib.geocoder.PlaceSuggestion
import se.gustavkarlsson.skylight.android.lib.permissions.Permissions
import se.gustavkarlsson.skylight.android.lib.places.Place
import se.gustavkarlsson.skylight.android.lib.places.PlaceId
import se.gustavkarlsson.skylight.android.lib.reversegeocoder.ReverseGeocodingResult
import se.gustavkarlsson.skylight.android.lib.settings.NotificationTriggerLevels

internal sealed interface State {
    val permissions: Permissions
    val currentLocationName: Loadable<ReverseGeocodingResult>
    val selectedAuroraReport: LoadableAuroraReport
    val search: Search
    val selectedPlace: Place?

    data class Loading(
        override val permissions: Permissions,
        override val currentLocationName: Loadable<ReverseGeocodingResult>,
        val selectedPlaceId: PlaceId?,
        override val selectedAuroraReport: LoadableAuroraReport,
        override val search: Search,
        val places: List<Place>?,
        val notificationTriggerLevels: NotificationTriggerLevels?,
    ) : State {
        override val selectedPlace: Place?
            get() = places.orEmpty().firstOrNull { place ->
                place.id == selectedPlaceId
            }
    }

    data class Ready(
        override val permissions: Permissions,
        override val currentLocationName: Loadable<ReverseGeocodingResult>,
        val selectedPlaceId: PlaceId,
        override val selectedAuroraReport: LoadableAuroraReport,
        override val search: Search,
        val places: List<Place>,
        val notificationTriggerLevels: NotificationTriggerLevels,
    ) : State {
        override val selectedPlace: Place
            get() = places.firstOrNull { place ->
                place.id == selectedPlaceId
            } ?: error("No place with place id $selectedPlaceId in $places")

        val selectedPlaceTriggerLevel: TriggerLevel
            get() = notificationTriggerLevels[selectedPlaceId]
    }
}

internal sealed class Search {
    object Inactive : Search()
    sealed class Active : Search() {
        abstract val query: String
        abstract val suggestions: List<PlaceSuggestion>

        data class Blank(
            override val query: String,
        ) : Active() {
            override val suggestions = emptyList<PlaceSuggestion>()
        }

        data class Filled(
            override val query: String,
            override val suggestions: List<PlaceSuggestion>,
        ) : Active()

        data class Error(
            override val query: String,
            val text: TextRef,
        ) : Active() {
            override val suggestions get() = emptyList<PlaceSuggestion>()
        }
    }
}

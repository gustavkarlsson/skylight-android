package se.gustavkarlsson.skylight.android.feature.main.state

import arrow.core.NonEmptyList
import com.ioki.textref.TextRef
import se.gustavkarlsson.skylight.android.core.entities.Loadable
import se.gustavkarlsson.skylight.android.lib.aurora.LoadableAuroraReport
import se.gustavkarlsson.skylight.android.lib.geocoder.PlaceSuggestion
import se.gustavkarlsson.skylight.android.lib.location.LocationResult
import se.gustavkarlsson.skylight.android.lib.location.LocationServiceStatus
import se.gustavkarlsson.skylight.android.lib.permissions.Permissions
import se.gustavkarlsson.skylight.android.lib.places.Place
import se.gustavkarlsson.skylight.android.lib.reversegeocoder.ReverseGeocodingResult
import se.gustavkarlsson.skylight.android.lib.settings.Settings

internal sealed interface State {
    val permissions: Permissions
    val locationServiceStatus: LocationServiceStatus?
    val currentLocation: Loadable<LocationResult>
    val currentLocationName: Loadable<ReverseGeocodingResult>
    val selectedAuroraReport: LoadableAuroraReport
    val search: Search
    val selectedPlace: Place?
    val places: NonEmptyList<Place>?
    val settings: Settings?

    data class Loading(
        override val permissions: Permissions,
        override val locationServiceStatus: LocationServiceStatus?,
        override val currentLocation: Loadable<LocationResult>,
        override val currentLocationName: Loadable<ReverseGeocodingResult>,
        override val selectedPlace: Place?,
        override val selectedAuroraReport: LoadableAuroraReport,
        override val search: Search,
        override val places: NonEmptyList<Place>?,
        override val settings: Settings?,
    ) : State

    data class Ready(
        override val permissions: Permissions,
        override val locationServiceStatus: LocationServiceStatus,
        override val currentLocation: Loadable<LocationResult>,
        override val currentLocationName: Loadable<ReverseGeocodingResult>,
        override val selectedPlace: Place,
        override val selectedAuroraReport: LoadableAuroraReport,
        override val search: Search,
        override val places: NonEmptyList<Place>,
        override val settings: Settings,
        val placeToDelete: Place.Saved?,
    ) : State
}

internal sealed interface Search {
    object Inactive : Search
    sealed interface Active : Search {
        val query: String
        val suggestions: List<PlaceSuggestion>

        data class Blank(
            override val query: String,
        ) : Active {
            override val suggestions = emptyList<PlaceSuggestion>()
        }

        data class Filled(
            override val query: String,
            override val suggestions: List<PlaceSuggestion>,
        ) : Active

        data class Error(
            override val query: String,
            val text: TextRef,
        ) : Active {
            override val suggestions get() = emptyList<PlaceSuggestion>()
        }
    }
}

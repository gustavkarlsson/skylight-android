package se.gustavkarlsson.skylight.android.feature.main

import com.ioki.textref.TextRef
import se.gustavkarlsson.skylight.android.lib.aurora.LoadableAuroraReport
import se.gustavkarlsson.skylight.android.lib.geocoder.PlaceSuggestion
import se.gustavkarlsson.skylight.android.lib.permissions.Access
import se.gustavkarlsson.skylight.android.lib.places.Place

internal data class State(
    val locationAccess: Access,
    val selectedPlace: Place,
    val selectedAuroraReport: LoadableAuroraReport,
    // FIXME combine search into one object
    val search: Search,
    val places: List<Place>,
)

internal sealed class Search {
    object Closed : Search()
    data class Open(val query: String, val suggestions: Suggestions, val error: TextRef?) : Search()
}

internal data class Suggestions(val query: String, val items: List<PlaceSuggestion>)

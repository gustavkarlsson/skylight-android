package se.gustavkarlsson.skylight.android.feature.main

import se.gustavkarlsson.skylight.android.lib.aurora.LoadableAuroraReport
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
    object Unfocused : Search()
    data class Focused(val text: String) : Search()
}

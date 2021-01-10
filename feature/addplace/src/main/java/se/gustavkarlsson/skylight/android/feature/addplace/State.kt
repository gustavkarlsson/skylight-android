package se.gustavkarlsson.skylight.android.feature.addplace

import se.gustavkarlsson.skylight.android.lib.geocoder.PlaceSuggestion

internal data class State(
    val query: String = "",
    val currentSearch: String? = null,
    val suggestions: Suggestions = Suggestions("", emptyList()),
) {
    val isSearchingForQuery: Boolean get() = query == currentSearch
    val isSuggestionsUpToDate: Boolean get() = query == suggestions.query

    data class Suggestions(val query: String, val items: List<PlaceSuggestion>)
}

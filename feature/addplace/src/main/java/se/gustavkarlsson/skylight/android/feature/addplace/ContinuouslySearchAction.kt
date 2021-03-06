package se.gustavkarlsson.skylight.android.feature.addplace

import com.ioki.textref.TextRef
import kotlinx.coroutines.delay
import org.threeten.bp.Duration
import se.gustavkarlsson.conveyor.Action
import se.gustavkarlsson.conveyor.UpdatableStateFlow
import se.gustavkarlsson.skylight.android.lib.geocoder.Geocoder
import se.gustavkarlsson.skylight.android.lib.geocoder.GeocodingResult

internal class ContinuouslySearchAction(
    private val geocoder: Geocoder,
    private val querySampleDelay: Duration,
) : Action<State> {
    override suspend fun execute(state: UpdatableStateFlow<State>) {
        var lastQuery: String? = null
        while (true) {
            if (state.value.query != lastQuery) {
                trySearch(state)
            }
            lastQuery = state.value.query
            delay(querySampleDelay.toMillis())
        }
    }

    private suspend fun trySearch(state: UpdatableStateFlow<State>) {
        var shouldSearch = false
        val updatedState = state.update {
            if (isSuggestionsUpToDate || isSearchingForQuery) {
                this
            } else {
                shouldSearch = true
                copy(currentSearch = query)
            }
        }
        if (shouldSearch) {
            doSearch(updatedState.query, state)
        }
    }

    private suspend fun doSearch(query: String, stateAccess: UpdatableStateFlow<State>) {
        val result = geocoder.geocode(query)
        stateAccess.update {
            when (result) {
                is GeocodingResult.Success -> {
                    if (this.query == query) {
                        val suggestions = State.Suggestions(query, result.suggestions)
                        updateState(query, suggestionsToSet = suggestions)
                    } else {
                        updateState(query)
                    }
                }
                GeocodingResult.Failure.Io -> {
                    val error = TextRef.stringRes(R.string.place_search_failed_io)
                    updateState(query, errorToSet = error)
                }
                GeocodingResult.Failure.ServerError -> {
                    val error = TextRef.stringRes(R.string.place_search_failed_server_response)
                    updateState(query, errorToSet = error)
                }
                GeocodingResult.Failure.Unknown -> {
                    val error = TextRef.stringRes(R.string.place_search_failed_generic)
                    updateState(query, errorToSet = error)
                }
            }
        }
    }

    private fun State.updateState(
        completedSearch: String,
        suggestionsToSet: State.Suggestions? = null,
        errorToSet: TextRef? = null,
    ): State {
        val currentSearch = if (completedSearch == currentSearch) {
            null
        } else currentSearch
        val error = errorToSet ?: error
        val suggestions = suggestionsToSet ?: suggestions
        return copy(currentSearch = currentSearch, error = error, suggestions = suggestions)
    }
}

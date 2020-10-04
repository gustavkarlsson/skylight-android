package se.gustavkarlsson.skylight.android.feature.addplace

import com.ioki.textref.TextRef
import kotlinx.coroutines.delay
import org.threeten.bp.Duration
import se.gustavkarlsson.conveyor.Action
import se.gustavkarlsson.conveyor.StateAccess
import se.gustavkarlsson.skylight.android.lib.geocoder.Geocoder
import se.gustavkarlsson.skylight.android.lib.geocoder.GeocodingResult

internal class ContinuouslySearchAction(
    private val geocoder: Geocoder,
    private val onError: (TextRef) -> Unit,
    private val querySampleDelay: Duration,
) : Action<State> {
    override suspend fun execute(stateAccess: StateAccess<State>) {
        while (true) {
            trySearch(stateAccess)
            delay(querySampleDelay.toMillis())
        }
    }

    private suspend fun trySearch(stateAccess: StateAccess<State>) {
        var shouldSearch = false
        val state = stateAccess.update { state ->
            if (state.isSuggestionsUpToDate || state.isSearchingForQuery) {
                state
            } else {
                shouldSearch = true
                state.copy(currentSearch = state.query)
            }
        }
        if (shouldSearch) {
            doSearch(state.query, stateAccess)
        }
    }

    private suspend fun doSearch(query: String, stateAccess: StateAccess<State>) {
        var errorMessage: TextRef? = null
        val result = geocoder.geocode(query)
        stateAccess.update { state ->
            when (result) {
                is GeocodingResult.Success -> {
                    if (state.query == query) {
                        val suggestions = State.Suggestions(query, result.suggestions)
                        state.updateCurrentSearch(query).copy(suggestions = suggestions)
                    } else {
                        state.updateCurrentSearch(query)
                    }
                }
                GeocodingResult.Failure.Io -> {
                    errorMessage = TextRef.stringRes(R.string.place_search_failed_io)
                    state.updateCurrentSearch(query)
                }
                GeocodingResult.Failure.ServerError -> {
                    errorMessage = TextRef.stringRes(R.string.place_search_failed_server_response)
                    state.updateCurrentSearch(query)
                }
                GeocodingResult.Failure.Unknown -> {
                    errorMessage = TextRef.stringRes(R.string.place_search_failed_generic)
                    state.updateCurrentSearch(query)
                }
            }
        }
        errorMessage?.let(onError)
    }

    private fun State.updateCurrentSearch(query: String): State =
        if (currentSearch == query) {
            copy(currentSearch = null)
        } else {
            this
        }
}

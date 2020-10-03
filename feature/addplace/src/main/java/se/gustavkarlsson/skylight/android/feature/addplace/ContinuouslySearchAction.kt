package se.gustavkarlsson.skylight.android.feature.addplace

import com.ioki.textref.TextRef
import kotlinx.coroutines.delay
import org.threeten.bp.Duration
import se.gustavkarlsson.conveyor.Action
import se.gustavkarlsson.conveyor.UpdateState
import se.gustavkarlsson.skylight.android.lib.geocoder.Geocoder
import se.gustavkarlsson.skylight.android.lib.geocoder.GeocodingResult

internal class ContinuouslySearchAction(
    private val geocoder: Geocoder,
    private val onError: (TextRef) -> Unit,
    private val querySampleDelay: Duration,
) : Action<State> {
    override suspend fun execute(updateState: UpdateState<State>) {
        while (true) {
            trySearch(updateState)
            delay(querySampleDelay.toMillis())
        }
    }

    private suspend fun trySearch(updateState: UpdateState<State>) {
        var shouldSearch = false
        val state = updateState { state ->
            if (state.isSuggestionsUpToDate || state.isSearchingForQuery) {
                state
            } else {
                shouldSearch = true
                state.copy(currentSearch = state.query)
            }
        }
        if (shouldSearch) {
            doSearch(state.query, updateState)
        }
    }

    private suspend fun doSearch(query: String, updateState: UpdateState<State>) {
        var errorMessage: TextRef? = null
        val result = geocoder.geocode(query)
        updateState { state ->
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

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
    private val onError: (TextRef) -> Unit,
    private val querySampleDelay: Duration,
) : Action<State> {
    override suspend fun execute(state: UpdatableStateFlow<State>) {
        while (true) {
            trySearch(state)
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
        var errorMessage: TextRef? = null
        val result = geocoder.geocode(query)
        stateAccess.update {
            when (result) {
                is GeocodingResult.Success -> {
                    if (this.query == query) {
                        val suggestions = State.Suggestions(query, result.suggestions)
                        updateCurrentSearch(query).copy(suggestions = suggestions)
                    } else {
                        updateCurrentSearch(query)
                    }
                }
                GeocodingResult.Failure.Io -> {
                    errorMessage = TextRef.stringRes(R.string.place_search_failed_io)
                    updateCurrentSearch(query)
                }
                GeocodingResult.Failure.ServerError -> {
                    errorMessage = TextRef.stringRes(R.string.place_search_failed_server_response)
                    updateCurrentSearch(query)
                }
                GeocodingResult.Failure.Unknown -> {
                    errorMessage = TextRef.stringRes(R.string.place_search_failed_generic)
                    updateCurrentSearch(query)
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

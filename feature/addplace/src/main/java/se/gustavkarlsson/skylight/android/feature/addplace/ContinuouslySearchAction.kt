package se.gustavkarlsson.skylight.android.feature.addplace

import com.ioki.textref.TextRef
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import org.threeten.bp.Duration
import se.gustavkarlsson.conveyor.Change
import se.gustavkarlsson.conveyor.Command
import se.gustavkarlsson.conveyor.CommandIssuer
import se.gustavkarlsson.conveyor.actions.MultiAction
import se.gustavkarlsson.conveyor.actions.SingleAction
import se.gustavkarlsson.conveyor.actions.VoidAction
import se.gustavkarlsson.conveyor.only
import se.gustavkarlsson.conveyor.plus
import se.gustavkarlsson.skylight.android.lib.geocoder.Geocoder
import se.gustavkarlsson.skylight.android.lib.geocoder.GeocodingResult

internal class ContinuouslySearchAction(
    private val geocoder: Geocoder,
    private val onError: (TextRef) -> Unit,
    private val querySampleDelay: Duration,
) : MultiAction<State>() {
    override suspend fun execute(issuer: CommandIssuer<State>) {
        while (true) {
            issuer.issue(TrySearchCommand(geocoder, onError))
            try {
                delay(querySampleDelay.toMillis())
            } catch (e: CancellationException) {
                return
            }
        }
    }
}

private class TrySearchCommand(
    private val geocoder: Geocoder,
    private val onError: (TextRef) -> Unit,
) : Command<State> {
    override fun reduce(state: State): Change<State> =
        if (state.isSuggestionsUpToDate || state.isSearchingForQuery) {
            state.only()
        } else {
            state.copy(currentSearch = state.query) + ActualSearchAction(geocoder, onError, state.query)
        }
}

private class ActualSearchAction(
    private val geocoder: Geocoder,
    private val onError: (TextRef) -> Unit,
    private val query: String,
) : SingleAction<State>() {
    override suspend fun execute(): Command<State> =
        when (val result = geocoder.geocode(query)) {
            is GeocodingResult.Success -> Command { state ->
                if (state.query == query) {
                    val suggestions = State.Suggestions(query, result.suggestions)
                    state.updateCurrentSearch(query).copy(suggestions = suggestions).only()
                } else {
                    state.updateCurrentSearch(query).only()
                }
            }
            GeocodingResult.Failure.Io -> Command { state ->
                state.updateCurrentSearch(query) +
                    ShowErrorAction(onError, TextRef.stringRes(R.string.place_search_failed_io))
            }
            GeocodingResult.Failure.ServerError -> Command { state ->
                state.updateCurrentSearch(query) +
                    ShowErrorAction(onError, TextRef.stringRes(R.string.place_search_failed_server_response))
            }
            GeocodingResult.Failure.Unknown -> Command { state ->
                state.updateCurrentSearch(query) +
                    ShowErrorAction(onError, TextRef.stringRes(R.string.place_search_failed_generic))
            }
        }

    private fun State.updateCurrentSearch(query: String): State =
        if (currentSearch == query) {
            copy(currentSearch = null)
        } else {
            this
        }
}

private class ShowErrorAction(
    private val onError: (TextRef) -> Unit,
    private val message: TextRef,
) : VoidAction<State>() {
    override suspend fun execute() = onError(message)
}

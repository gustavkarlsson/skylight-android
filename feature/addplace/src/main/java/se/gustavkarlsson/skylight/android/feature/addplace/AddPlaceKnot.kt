package se.gustavkarlsson.skylight.android.feature.addplace

import com.ioki.textref.TextRef
import de.halfbit.knot.Knot
import de.halfbit.knot.knot
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.rx2.asFlow
import kotlinx.coroutines.rx2.asObservable
import org.threeten.bp.Duration
import se.gustavkarlsson.skylight.android.lib.geocoder.Geocoder
import se.gustavkarlsson.skylight.android.lib.geocoder.GeocodingResult
import se.gustavkarlsson.skylight.android.lib.geocoder.PlaceSuggestion
import java.util.concurrent.TimeUnit

internal data class State(
    val query: String = "",
    val suggestions: List<PlaceSuggestion> = emptyList(),
    val ongoingSearches: Long = 0
) {
    val isSearching: Boolean get() = ongoingSearches > 0
}

internal sealed class Change {
    data class Query(val query: String) : Change()
    data class SearchesSkipped(val skipped: Int) : Change()
    data class SearchFinished(val suggestions: List<PlaceSuggestion>) : Change()
    data class SearchFailed(val message: TextRef) : Change()
}

internal sealed class Action {
    data class Search(val query: String) : Action()
    data class ShowError(val message: TextRef) : Action()
}

internal typealias AddPlaceKnot = Knot<State, Change>

@FlowPreview
@ExperimentalCoroutinesApi
internal fun createKnot(
    geocoder: Geocoder,
    querySampleDelay: Duration,
    onError: (TextRef) -> Unit
): AddPlaceKnot = knot<State, Change, Action> {

    state {
        initial = State()
    }

    changes {
        reduce { change ->
            when (change) {
                is Change.Query -> {
                    val query = change.query.trim()
                    when {
                        this.query == query -> this.only
                        query.isEmpty() -> copy(query = query).only
                        else -> copy(
                            query = query,
                            ongoingSearches = ongoingSearches + 1
                        ) + Action.Search(query)
                    }
                }
                is Change.SearchesSkipped ->
                    copy(ongoingSearches = ongoingSearches - change.skipped).only
                is Change.SearchFinished ->
                    copy(
                        suggestions = change.suggestions,
                        ongoingSearches = ongoingSearches - 1
                    ).only
                is Change.SearchFailed ->
                    copy(ongoingSearches = ongoingSearches - 1) + Action.ShowError(change.message)
            }
        }
    }

    actions {
        perform<Action.Search> {
            map(Action.Search::query)
                .buffer(querySampleDelay.toMillis(), TimeUnit.MILLISECONDS)
                .asFlow()
                .filter { it.isNotEmpty() }
                .flatMapConcat { texts ->
                    flow {
                        emit(Change.SearchesSkipped(texts.size - 1))

                        val change = when (val result = geocoder.geocode(texts.last())) {
                            is GeocodingResult.Success ->
                                Change.SearchFinished(result.suggestions)
                            GeocodingResult.Failure.Io ->
                                Change.SearchFailed(TextRef.stringRes(R.string.place_search_failed_io))
                            GeocodingResult.Failure.ServerError ->
                                Change.SearchFailed(TextRef.stringRes(R.string.place_search_failed_server_response))
                            GeocodingResult.Failure.Unknown ->
                                Change.SearchFailed(TextRef.stringRes(R.string.place_search_failed_generic))
                        }

                        emit(change)
                    }
                }
                .asObservable()
        }
        watch<Action.ShowError> {
            onError(it.message)
        }
    }
}

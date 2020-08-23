package se.gustavkarlsson.skylight.android.feature.addplace

import com.ioki.textref.TextRef
import de.halfbit.knot.Knot
import de.halfbit.knot.knot
import io.reactivex.Observable
import io.reactivex.Scheduler
import kotlinx.coroutines.rx2.rxSingle
import org.threeten.bp.Duration
import se.gustavkarlsson.skylight.android.core.utils.buffer
import se.gustavkarlsson.skylight.android.lib.geocoder.Geocoder
import se.gustavkarlsson.skylight.android.lib.geocoder.GeocodingResult
import se.gustavkarlsson.skylight.android.lib.geocoder.PlaceSuggestion

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

internal fun createKnot(
    geocoder: Geocoder,
    querySampleDelay: Duration,
    onError: (TextRef) -> Unit,
    observeScheduler: Scheduler
): AddPlaceKnot = knot<State, Change, Action> {

    state {
        initial = State()
        observeOn = observeScheduler
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
                .buffer(querySampleDelay)
                .filter { it.isNotEmpty() }
                .flatMap { texts ->
                    Observable.concat(
                        Observable.just(Change.SearchesSkipped(texts.size - 1)),
                        rxSingle { geocoder.geocode(texts.last()) }
                            .map {
                                when (val result = it) {
                                    is GeocodingResult.Success ->
                                        Change.SearchFinished(result.suggestions)
                                    GeocodingResult.Failure.Io ->
                                        Change.SearchFailed(
                                            TextRef.stringRes(R.string.place_search_failed_io)
                                        )
                                    GeocodingResult.Failure.ServerError -> Change.SearchFailed(
                                        TextRef.stringRes(R.string.place_search_failed_server_response)
                                    )
                                    GeocodingResult.Failure.Unknown ->
                                        Change.SearchFailed(
                                            TextRef.stringRes(R.string.place_search_failed_generic)
                                        )
                                }
                            }
                            .toObservable()
                    )
                }
        }
        watch<Action.ShowError> {
            onError(it.message)
        }
    }
}

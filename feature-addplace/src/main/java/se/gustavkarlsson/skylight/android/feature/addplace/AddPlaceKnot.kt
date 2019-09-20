package se.gustavkarlsson.skylight.android.feature.addplace

import de.halfbit.knot.Knot
import de.halfbit.knot.knot
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import org.threeten.bp.Duration
import se.gustavkarlsson.skylight.android.entities.PlaceSuggestion
import se.gustavkarlsson.skylight.android.extensions.buffer
import se.gustavkarlsson.skylight.android.extensions.delay
import se.gustavkarlsson.skylight.android.lib.geocoder.Geocoder

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
	data class SearchFinished(val result: List<PlaceSuggestion>) : Change()
}

internal sealed class Action {
	data class Search(val query: String) : Action()
}

internal typealias AddPlaceKnot = Knot<State, Change>

internal fun createKnot(
	geocoder: Geocoder,
	searchSampleDelay: Duration,
	retryDelay: Duration
): AddPlaceKnot = knot<State, Change, Action> {

	state {
		initial = State()
		observeOn = AndroidSchedulers.mainThread()
	}

	changes {
		reduce { change ->
			when (change) {
				is Change.Query -> {
					val query = change.query.trim()
					when {
						this.query == query -> this.only
						query.isEmpty() -> copy(query = query).only
						else -> copy(query = query, ongoingSearches = ongoingSearches + 1) + Action.Search(query)
					}
				}
				is Change.SearchesSkipped ->
					copy(ongoingSearches = ongoingSearches - change.skipped).only
				is Change.SearchFinished ->
					copy(suggestions = change.result, ongoingSearches = ongoingSearches - 1).only
			}
		}
	}

	actions {
		perform<Action.Search> {
			this
				.map(Action.Search::query)
				.buffer(searchSampleDelay)
				.filter { it.isNotEmpty() }
				.flatMap { texts ->
					Observable.concat(
						Observable.just(Change.SearchesSkipped(texts.size - 1)),
						geocoder.geocode(texts.last())
							.retryWhen { it.delay(retryDelay) } // FIXME map to failure instead
							.map(Change::SearchFinished)
							.toObservable()
					)
				}
		}
	}
}

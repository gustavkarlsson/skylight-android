package se.gustavkarlsson.skylight.android.feature.addplace

import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import org.threeten.bp.Duration
import se.gustavkarlsson.krate.core.Store
import se.gustavkarlsson.krate.core.dsl.buildStore
import se.gustavkarlsson.skylight.android.entities.PlaceSuggestion
import se.gustavkarlsson.skylight.android.extensions.delay
import se.gustavkarlsson.skylight.android.extensions.delaySubscription
import se.gustavkarlsson.skylight.android.services.Geocoder

internal typealias AddPlaceStore = Store<State, Command, Result>

internal data class State(
	val suggestions: List<PlaceSuggestion> = emptyList(),
	val searching: Boolean = false
)

internal sealed class Command {
	data class Search(val text: String) : Command()
}

internal sealed class Result {
	data class Suggestions(val suggestions: List<PlaceSuggestion> = emptyList()) : Result()
	object Searching : Result()
}

internal fun createStore(
	geocoder: Geocoder,
	searchDebounceDelay: Duration,
	retryDelay: Duration
): AddPlaceStore = buildStore {

	states {
		initial = State()
		observeScheduler = AndroidSchedulers.mainThread()
	}

	commands {
		transform<Command.Search> { commands ->
			commands.switchMap { (text) ->
				if (text.isBlank()) {
					Flowable.just(Result.Suggestions())
				} else {
					Flowable.concat(
						Flowable.just<Result>(Result.Searching),
						geocoder.geocode(text)
							.retryWhen { it.delay(retryDelay) }
							.delaySubscription(searchDebounceDelay)
							.map(Result::Suggestions)
							.toFlowable()
					)
				}
			}
		}
	}

	results {
		reduce { state, result ->
			when (result) {
				Result.Searching -> state.copy(searching = true)
				is Result.Suggestions -> state.copy(suggestions = result.suggestions, searching = false)
			}
		}
	}
}

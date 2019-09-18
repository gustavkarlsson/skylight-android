package se.gustavkarlsson.skylight.android.feature.addplace

import androidx.lifecycle.ViewModel
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Observable
import se.gustavkarlsson.skylight.android.entities.Location
import se.gustavkarlsson.skylight.android.entities.PlaceSuggestion
import se.gustavkarlsson.skylight.android.lib.navigation.NavItem
import se.gustavkarlsson.skylight.android.lib.navigation.Navigator
import se.gustavkarlsson.skylight.android.lib.places.PlacesRepository

internal class AddPlaceViewModel(
	private val placesRepository: PlacesRepository,
	private val knot: AddPlaceKnot,
	private val navigator: Navigator,
	private val destination: NavItem?
) : ViewModel() {
	private val state = knot.state.toFlowable(BackpressureStrategy.LATEST)

	override fun onCleared() {
		super.onCleared()
		knot.dispose()
	}

	private val openSaveDialogRelay = PublishRelay.create<SaveDialogData>()
	val openSaveDialog: Observable<SaveDialogData> = openSaveDialogRelay

	fun onSearchTextChanged(newText: String) = knot.change.accept(Change.Query(newText))

	val searchResultItems: Flowable<List<SearchResultItem>> = state
		.map(State::suggestions)
		.distinctUntilChanged()
		.map { suggestions ->
			suggestions.map { it.toSearchResultItem() }
		}

	private fun PlaceSuggestion.toSearchResultItem() =
		SearchResultItem(fullName) {
			val dialogData = SaveDialogData(simpleName) { finalName ->
				addPlaceAndLeave(finalName, location)
			}
			openSaveDialogRelay.accept(dialogData)
		}

	private fun addPlaceAndLeave(name: String, location: Location) {
		placesRepository.add(name, location)
		destination?.let(navigator::replaceScope) ?: navigator.pop()
	}
	private val resultState: Flowable<ResultState> = state
		.map { state ->
			when {
				state.query.isBlank() -> ResultState.EMPTY
				state.searches > 0 && state.suggestions.isEmpty() -> ResultState.SEARCHING
				state.suggestions.isEmpty() -> ResultState.NO_SUGGESTIONS
				else -> ResultState.SUGGESTIONS
			}
		}

	val isEmptyVisible: Flowable<Boolean> = resultState.map { it == ResultState.EMPTY }

	val isSearchingVisible: Flowable<Boolean> = resultState.map { it == ResultState.SEARCHING }

	val isNoSuggestionsVisible: Flowable<Boolean> = resultState.map { it == ResultState.NO_SUGGESTIONS }

	val isSuggestionsVisible: Flowable<Boolean> = resultState.map { it == ResultState.SUGGESTIONS }
}

internal enum class ResultState {
	EMPTY, SEARCHING, NO_SUGGESTIONS, SUGGESTIONS
}

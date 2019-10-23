package se.gustavkarlsson.skylight.android.feature.addplace

import android.text.SpannedString
import androidx.core.text.bold
import androidx.core.text.buildSpannedString
import androidx.lifecycle.ViewModel
import com.ioki.textref.TextRef
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Observable
import se.gustavkarlsson.skylight.android.entities.Location
import se.gustavkarlsson.skylight.android.extensions.mapNotNull
import se.gustavkarlsson.skylight.android.entities.PlaceSuggestion
import se.gustavkarlsson.skylight.android.lib.navigation.NavItem
import se.gustavkarlsson.skylight.android.lib.navigation.Navigator
import se.gustavkarlsson.skylight.android.services.PlacesRepository

internal class AddPlaceViewModel(
	private val placesRepository: PlacesRepository,
	private val knot: AddPlaceKnot,
	private val navigator: Navigator,
	private val destination: NavItem?,
	val errorMessages: Observable<TextRef>
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
		.filter { it.isNotEmpty() }
		.map { suggestions ->
			suggestions.map { it.toSearchResultItem() }
		}

	private fun PlaceSuggestion.toSearchResultItem() =
		SearchResultItem(createText()) {
			val dialogData = SaveDialogData(simpleName) { finalName ->
				addPlaceAndLeave(finalName, location)
			}
			openSaveDialogRelay.accept(dialogData)
		}

	private fun PlaceSuggestion.createText(): SpannedString {
		val subTitle = createSubTitle()
		return buildSpannedString {
			bold { append(simpleName) }
			if (subTitle.isNotBlank()) {
				appendln()
				append(subTitle)
			}
		}
	}

	private fun PlaceSuggestion.createSubTitle() =
		fullName
			.removePrefix(simpleName)
			.dropWhile { !it.isLetterOrDigit() }

	private fun addPlaceAndLeave(name: String, location: Location) {
		placesRepository.add(name, location)
		destination?.let(navigator::replaceScope) ?: navigator.pop()
	}

	private val resultState: Flowable<ResultState> = state
		.mapNotNull { state ->
			when {
				state.query.isBlank() -> ResultState.EMPTY
				state.isSearching -> null
				state.suggestions.isEmpty() -> ResultState.NO_SUGGESTIONS
				else -> ResultState.SUGGESTIONS
			}
		}
		.distinctUntilChanged()

	val isEmptyVisible: Flowable<Boolean> = resultState.map { it == ResultState.EMPTY }

	val isNoSuggestionsVisible: Flowable<Boolean> =
		resultState.map { it == ResultState.NO_SUGGESTIONS }

	val isSuggestionsVisible: Flowable<Boolean> = resultState.map { it == ResultState.SUGGESTIONS }
}

internal enum class ResultState {
	EMPTY, NO_SUGGESTIONS, SUGGESTIONS
}

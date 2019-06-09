package se.gustavkarlsson.skylight.android.feature.addplace

import androidx.lifecycle.ViewModel
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Flowable
import io.reactivex.Observable
import se.gustavkarlsson.skylight.android.entities.Location
import se.gustavkarlsson.skylight.android.entities.PlaceSuggestion
import se.gustavkarlsson.skylight.android.lib.ui.Navigator
import se.gustavkarlsson.skylight.android.krate.SkylightStore
import se.gustavkarlsson.skylight.android.krate.Command as MainCommand

internal class AddPlaceViewModel(
	private val mainStore: SkylightStore,
	private val addPlaceStore: AddPlaceStore,
	private val navigator: Navigator
) : ViewModel() {

	private val openSaveDialogRelay = PublishRelay.create<SaveDialogData>()
	val openSaveDialog: Observable<SaveDialogData> = openSaveDialogRelay

	fun onSearchTextChanged(newText: String) = addPlaceStore.issue(Command.Search(newText))

	val searchResultItems: Flowable<List<SearchResultItem>> = addPlaceStore.states
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
		mainStore.issue(MainCommand.AddPlace(name, location))
		if (navigator.backStackSize == 0) {
			navigator.navigate("main", false)
		} else {
			navigator.goBack()
		}
	}

	val isLoading: Flowable<Boolean> = addPlaceStore.states
		.map(State::searching)
		.distinctUntilChanged()
}

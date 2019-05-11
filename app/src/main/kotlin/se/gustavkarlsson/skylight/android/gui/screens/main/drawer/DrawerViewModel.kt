package se.gustavkarlsson.skylight.android.gui.screens.main.drawer

import androidx.lifecycle.ViewModel
import io.reactivex.Flowable
import se.gustavkarlsson.skylight.android.krate.SelectPlaceCommand
import se.gustavkarlsson.skylight.android.krate.SkylightState
import se.gustavkarlsson.skylight.android.krate.SkylightStore

class DrawerViewModel(
	private val store: SkylightStore
) : ViewModel() {
	val places: Flowable<List<PlaceItem>> =
		store.states
			.map(::createPlaceItems)
			.distinctUntilChanged()

	private fun createPlaceItems(state: SkylightState): List<PlaceItem> {
		val selectedId = state.selectedPlace.id
		return state.allPlaces.map {
			val isActive = it.id == selectedId
			PlaceItem(isActive, it.name) {
				store.issue(SelectPlaceCommand(it))
			}
		}
	}
}

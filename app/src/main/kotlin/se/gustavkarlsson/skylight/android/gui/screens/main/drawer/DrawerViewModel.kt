package se.gustavkarlsson.skylight.android.gui.screens.main.drawer

import androidx.lifecycle.ViewModel
import io.reactivex.Flowable
import se.gustavkarlsson.skylight.android.krate.Command
import se.gustavkarlsson.skylight.android.krate.State
import se.gustavkarlsson.skylight.android.krate.SkylightStore

class DrawerViewModel(
	private val store: SkylightStore
) : ViewModel() {
	val places: Flowable<List<PlaceItem>> =
		store.states
			.map(::createPlaceItems)
			.distinctUntilChanged()

	private fun createPlaceItems(state: State): List<PlaceItem> {
		return state.allPlaces.map {
			val isActive = it.id == state.selectedPlaceId
			PlaceItem(isActive, it.name) {
				store.issue(Command.SelectPlace(it.id))
			}
		}
	}
}

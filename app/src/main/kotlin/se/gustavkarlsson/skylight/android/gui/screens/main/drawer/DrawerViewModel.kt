package se.gustavkarlsson.skylight.android.gui.screens.main.drawer

import androidx.lifecycle.ViewModel
import com.ioki.textref.TextRef
import io.reactivex.Flowable
import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.krate.SelectPlaceCommand
import se.gustavkarlsson.skylight.android.krate.SkylightState
import se.gustavkarlsson.skylight.android.krate.SkylightStore

class DrawerViewModel(
	private val store: SkylightStore
) : ViewModel() {
	val places: Flowable<List<PlaceItem>> =
		store.states
			.map {
				listOf(getCurrentPlaceItem(it)) + getCustomPlaceItems(it)
			}
			.distinctUntilChanged()

	private fun getCurrentPlaceItem(state: SkylightState): PlaceItem {
		val isActive = state.selectedPlace == null
		val name = state.currentLocationAuroraReport?.locationName?.let { TextRef(it) }
			?: TextRef(R.string.main_your_location)
		return PlaceItem(isActive, name) {
			store.issue(SelectPlaceCommand(null))
		}
	}

	private fun getCustomPlaceItems(state: SkylightState): List<PlaceItem> {
		return state.customPlaces.map {
			val isActive = state.selectedPlace?.id == it.id
			val name = TextRef(it.name)
			PlaceItem(isActive, name) {
				store.issue(SelectPlaceCommand(it))
			}
		}
	}
}

package se.gustavkarlsson.skylight.android.gui.screens.main.drawer

import androidx.lifecycle.ViewModel
import com.ioki.textref.TextRef
import io.reactivex.Flowable
import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.entities.Place
import se.gustavkarlsson.skylight.android.krate.Command
import se.gustavkarlsson.skylight.android.krate.SkylightStore
import se.gustavkarlsson.skylight.android.krate.State
import se.gustavkarlsson.skylight.android.navigation.Navigator

class DrawerViewModel(
	private val store: SkylightStore,
	private val navigator: Navigator
) : ViewModel() {
	val places: Flowable<List<PlaceItem>> =
		store.states
			.map(::createPlaceItems)
			.distinctUntilChanged()

	private fun createPlaceItems(state: State): List<PlaceItem> {
		return state.allPlaces.map {
			val isActive = it.id == state.selectedPlaceId
			val icon = when (it) {
				is Place.Current -> R.drawable.ic_location_on_white_24dp
				is Place.Custom -> R.drawable.ic_map_white_24dp
			}
			PlaceItem(isActive, icon, it.name) {
				store.issue(Command.SelectPlace(it.id))
			}
		} + PlaceItem(false, R.drawable.ic_add_white_24dp, TextRef(R.string.add_place)) {
			// FIXME navigate to Add Place
		}
	}
}

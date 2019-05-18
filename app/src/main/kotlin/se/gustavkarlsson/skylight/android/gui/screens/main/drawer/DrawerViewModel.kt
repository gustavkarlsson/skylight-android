package se.gustavkarlsson.skylight.android.gui.screens.main.drawer

import androidx.lifecycle.ViewModel
import com.ioki.textref.TextRef
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.entities.Place
import se.gustavkarlsson.skylight.android.krate.Command
import se.gustavkarlsson.skylight.android.krate.SkylightStore
import se.gustavkarlsson.skylight.android.krate.State
import se.gustavkarlsson.skylight.android.navigation.Navigator
import se.gustavkarlsson.skylight.android.navigation.Screen

class DrawerViewModel(
	private val store: SkylightStore,
	private val navigator: Navigator
) : ViewModel() {
	val places: Flowable<List<PlaceItem>> =
		store.states
			.map(::createPlaceItems)
			.distinctUntilChanged()

	private val closeDrawerRelay = PublishRelay.create<Unit>()
	val closeDrawer: Flowable<Unit> =
		closeDrawerRelay.toFlowable(BackpressureStrategy.MISSING)

	private fun createPlaceItems(state: State): List<PlaceItem> {
		return state.places.map { place ->
			val isActive = place == state.selectedPlace
			val icon = when (place) {
				is Place.Current -> R.drawable.ic_location_on_white_24dp
				is Place.Custom -> R.drawable.ic_map_white_24dp
			}
			val onClick = {
				store.issue(Command.SelectPlace(place))
				closeDrawerRelay.accept(Unit)
			}
			val onLongClick: () -> Unit = if (place is Place.Custom) {
				{ store.issue(Command.RemovePlace(place.id)) }// FIXME show dialog instead
			} else { {} }
			PlaceItem(isActive, icon, place.name, onClick, onLongClick)
		} + createAddPlaceItem()
	}

	private fun createAddPlaceItem(): PlaceItem {
		val onClick = { navigator.navigate(Screen.PICK_PLACE) }
		val onLongClick = {}
		return PlaceItem(false, R.drawable.ic_add_white_24dp, TextRef(R.string.add_place), onClick, onLongClick)
	}
}

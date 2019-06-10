package se.gustavkarlsson.skylight.android.feature.main.gui.drawer

import androidx.annotation.DrawableRes
import androidx.lifecycle.ViewModel
import com.ioki.textref.TextRef
import com.jakewharton.rxrelay2.PublishRelay
import de.halfbit.knot.Knot
import io.reactivex.Observable
import se.gustavkarlsson.skylight.android.feature.main.Change
import se.gustavkarlsson.skylight.android.entities.Place
import se.gustavkarlsson.skylight.android.feature.main.State
import se.gustavkarlsson.skylight.android.feature.main.R
import se.gustavkarlsson.skylight.android.lib.places.PlacesRepository
import se.gustavkarlsson.skylight.android.lib.ui.Navigator

internal class DrawerViewModel(
	private val mainKnot: Knot<State, Change>,
	private val navigator: Navigator,
	private val placesRepository: PlacesRepository
) : ViewModel() {
	val places: Observable<List<DrawerItem>> =
		mainKnot.state
			.map(::createPlaceItems)
			.distinctUntilChanged()

	private val closeDrawerRelay = PublishRelay.create<Unit>()
	val closeDrawer: Observable<Unit> = closeDrawerRelay

	private val openRemoveLocationDialogRelay = PublishRelay.create<RemoveLocationDialogData>()
	val openRemoveLocationDialog: Observable<RemoveLocationDialogData> = openRemoveLocationDialogRelay

	private fun createPlaceItems(state: State): List<DrawerItem> =
		state.places.map { place ->
			val isActive = place == state.selectedPlace
			val icon = when (place) {
				is Place.Current -> R.drawable.ic_location_on_white_24dp
				is Place.Custom -> R.drawable.ic_map_white_24dp
			}
			val onClick = {
				mainKnot.change.accept(Change.SelectPlace(place))
				closeDrawerRelay.accept(Unit)
			}
			val onLongClick: () -> Unit = if (place is Place.Custom) {
				{
					val dialogData = RemoveLocationDialogData(TextRef(R.string.remove_thing, place.name)) {
						placesRepository.remove(place.id)
					}
					openRemoveLocationDialogRelay.accept(dialogData)
				}
			} else {
				{}
			}
			DrawerItem(isActive, icon, place.name, onClick, onLongClick)
		} + createAddPlaceItem()

	private fun createAddPlaceItem(): DrawerItem {
		val onClick = {
			closeDrawerRelay.accept(Unit)
			navigator.navigate("addplace", true)
		}
		val onLongClick = {}
		return DrawerItem(false, R.drawable.ic_add_white_24dp, TextRef(R.string.add_place), onClick, onLongClick)
	}
}

internal data class RemoveLocationDialogData(
	val title: TextRef,
	val onConfirm: () -> Unit
)

internal data class DrawerItem(
	val isActive: Boolean,
	@DrawableRes val icon: Int,
	val name: TextRef,
	val onClick: () -> Unit,
	val onLongClick: () -> Unit
)

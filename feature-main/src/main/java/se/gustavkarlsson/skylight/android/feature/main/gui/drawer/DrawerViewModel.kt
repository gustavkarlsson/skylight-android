package se.gustavkarlsson.skylight.android.feature.main.gui.drawer

import androidx.annotation.DrawableRes
import androidx.lifecycle.ViewModel
import com.ioki.textref.TextRef
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.Observables
import se.gustavkarlsson.skylight.android.feature.main.R
import se.gustavkarlsson.skylight.android.lib.navigation.NavItem
import se.gustavkarlsson.skylight.android.lib.navigation.Navigator
import se.gustavkarlsson.skylight.android.entities.Place
import se.gustavkarlsson.skylight.android.services.PlacesRepository
import se.gustavkarlsson.skylight.android.services.SelectedPlaceRepository

internal class DrawerViewModel(
	private val navigator: Navigator,
	private val placesRepository: PlacesRepository,
	private val selectedPlaceRepo: SelectedPlaceRepository,
	observeScheduler: Scheduler = AndroidSchedulers.mainThread()
) : ViewModel() {
	val places: Observable<List<DrawerItem>> =
		Observables.combineLatest(
			placesRepository.stream(),
			selectedPlaceRepo.stream(),
			::createPlaceItems
		).observeOn(observeScheduler)

	private val closeDrawerRelay = PublishRelay.create<Unit>()
	val closeDrawer: Observable<Unit> = closeDrawerRelay

	private val openRemoveLocationDialogRelay = PublishRelay.create<RemoveLocationDialogData>()
	val openRemoveLocationDialog: Observable<RemoveLocationDialogData> =
		openRemoveLocationDialogRelay

	private fun createPlaceItems(places: List<Place>, selected: Place) =
		places.map { place ->
			DrawerItem(
				isActive = place == selected,
				icon = createIcon(place),
				name = place.name,
				onClick = createOnClick(place),
				onLongClick = createOnLongClick(place)
			)
		} + createAddPlaceItem()

	private fun createIcon(place: Place) =
		when (place) {
			is Place.Current -> R.drawable.ic_location_on_white_24dp
			is Place.Custom -> R.drawable.ic_map_white_24dp
		}

	private fun createOnClick(place: Place): () -> Unit = {
		selectedPlaceRepo.set(place)
		closeDrawerRelay.accept(Unit)
	}

	private fun createOnLongClick(place: Place): () -> Unit =
		if (place is Place.Custom) {
			{
				val dialogData =
					RemoveLocationDialogData(TextRef(R.string.remove_thing, place.name)) {
						placesRepository.remove(place.id)
					}
				openRemoveLocationDialogRelay.accept(dialogData)
			}
		} else {
			{}
		}

	private fun createAddPlaceItem(): DrawerItem {
		val onClick = {
			closeDrawerRelay.accept(Unit)
			navigator.push(NavItem("addplace"))
		}
		val onLongClick = {}
		return DrawerItem(
			false,
			R.drawable.ic_add_white_24dp,
			TextRef(R.string.add_place),
			onClick,
			onLongClick
		)
	}
}

internal data class RemoveLocationDialogData(
	val title: TextRef,
	val onConfirm: () -> Unit
)

// FIXME get rid of onclicks
internal data class DrawerItem(
	val isActive: Boolean,
	@DrawableRes val icon: Int,
	val name: TextRef,
	val onClick: () -> Unit,
	val onLongClick: () -> Unit
)

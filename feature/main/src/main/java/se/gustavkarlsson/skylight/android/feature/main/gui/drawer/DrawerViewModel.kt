package se.gustavkarlsson.skylight.android.feature.main.gui.drawer

import androidx.annotation.DrawableRes
import com.ioki.textref.TextRef
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.rxkotlin.Observables
import javax.inject.Inject
import se.gustavkarlsson.skylight.android.core.Main
import se.gustavkarlsson.skylight.android.feature.main.R
import se.gustavkarlsson.skylight.android.lib.places.Place
import se.gustavkarlsson.skylight.android.lib.places.PlacesRepository
import se.gustavkarlsson.skylight.android.lib.places.SelectedPlaceRepository
import se.gustavkarlsson.skylight.android.lib.scopedservice.ScopedService

internal class DrawerViewModel @Inject constructor(
    private val placesRepository: PlacesRepository,
    private val selectedPlaceRepo: SelectedPlaceRepository,
    @Main observeScheduler: Scheduler
) : ScopedService {
    val drawerItems: Observable<List<DrawerItem>> =
        Observables.combineLatest(
            placesRepository.stream(),
            selectedPlaceRepo.stream(),
            ::createPlaceItems
        ).observeOn(observeScheduler)

    private val closeDrawerRelay = PublishRelay.create<Unit>()
    val closeDrawer: Observable<Unit> = closeDrawerRelay

    private val openRemovePlaceDialogRelay = PublishRelay.create<RemovePlaceDialogData>()
    val openRemovePlaceDialog: Observable<RemovePlaceDialogData> = openRemovePlaceDialogRelay

    private val navigateToAddPlaceRelay = PublishRelay.create<Unit>()
    val navigateToAddPlace: Observable<Unit> = navigateToAddPlaceRelay

    private fun createPlaceItems(places: List<Place>, selected: Place) =
        places.map { place ->
            DrawerItem(
                isActive = place == selected,
                icon = createIcon(place),
                text = place.name,
                clickEvent = DrawerEvent.PlaceClicked(place),
                longClickEvent = createLongClickEvent(place)
            )
        } + createAddPlaceItem()

    private fun createIcon(place: Place) =
        when (place) {
            is Place.Current -> R.drawable.ic_location_on
            is Place.Custom -> R.drawable.ic_map
        }

    private fun createLongClickEvent(place: Place) =
        if (place is Place.Custom) {
            DrawerEvent.PlaceLongClicked(place)
        } else null

    private fun createAddPlaceItem() =
        DrawerItem(
            isActive = false,
            icon = R.drawable.ic_add,
            text = TextRef.stringRes(R.string.add_place),
            clickEvent = DrawerEvent.AddPlaceClicked,
            longClickEvent = null
        )

    fun onEvent(event: DrawerEvent) {
        when (event) {
            is DrawerEvent.PlaceClicked -> selectPlace(event.place)
            is DrawerEvent.PlaceLongClicked -> showRemovePlaceDialog(event.place)
            DrawerEvent.AddPlaceClicked -> navigateToAddPlace()
        }
    }

    fun onRemovePlaceClicked(placeId: Long) = placesRepository.remove(placeId)

    private fun selectPlace(place: Place) {
        selectedPlaceRepo.set(place)
        closeDrawerRelay.accept(Unit)
    }

    private fun showRemovePlaceDialog(place: Place.Custom) {
        val dialogData = RemovePlaceDialogData(
            TextRef.stringRes(R.string.remove_thing, place.name),
            place.id
        )
        openRemovePlaceDialogRelay.accept(dialogData)
    }

    private fun navigateToAddPlace() {
        closeDrawerRelay.accept(Unit)
        navigateToAddPlaceRelay.accept(Unit)
    }
}

internal data class RemovePlaceDialogData(
    val title: TextRef,
    val placeId: Long
)

internal data class DrawerItem(
    val isActive: Boolean,
    @DrawableRes val icon: Int,
    val text: TextRef,
    val clickEvent: DrawerEvent?,
    val longClickEvent: DrawerEvent?
)

internal sealed class DrawerEvent {
    data class PlaceClicked(val place: Place) : DrawerEvent()
    data class PlaceLongClicked(val place: Place.Custom) : DrawerEvent()
    object AddPlaceClicked : DrawerEvent()
}

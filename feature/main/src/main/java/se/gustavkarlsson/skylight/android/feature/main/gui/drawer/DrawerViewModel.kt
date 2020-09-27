package se.gustavkarlsson.skylight.android.feature.main.gui.drawer

import androidx.annotation.DrawableRes
import com.ioki.textref.TextRef
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.combine
import se.gustavkarlsson.skylight.android.feature.main.R
import se.gustavkarlsson.skylight.android.lib.places.Place
import se.gustavkarlsson.skylight.android.lib.places.PlacesRepository
import se.gustavkarlsson.skylight.android.lib.places.SelectedPlaceRepository
import se.gustavkarlsson.skylight.android.lib.scopedservice.ScopedService
import javax.inject.Inject

@ExperimentalCoroutinesApi
@FlowPreview
internal class DrawerViewModel @Inject constructor(
    private val placesRepository: PlacesRepository,
    private val selectedPlaceRepo: SelectedPlaceRepository
) : ScopedService {
    val drawerItems: Flow<List<DrawerItem>> =
        combine(
            placesRepository.stream(),
            selectedPlaceRepo.stream()
        ) { places, selectedPlace ->
            createPlaceItems(places, selectedPlace)
        }

    private val closeDrawerChannel = BroadcastChannel<Unit>(Channel.BUFFERED)
    val closeDrawer: Flow<Unit> = closeDrawerChannel.asFlow()

    private val openRemovePlaceDialogChannel = BroadcastChannel<RemovePlaceDialogData>(Channel.BUFFERED)
    val openRemovePlaceDialog: Flow<RemovePlaceDialogData> = openRemovePlaceDialogChannel.asFlow()

    private val navigateToAddPlaceChannel = BroadcastChannel<Unit>(Channel.BUFFERED)
    val navigateToAddPlace: Flow<Unit> = navigateToAddPlaceChannel.asFlow()

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

    suspend fun onRemovePlaceClicked(placeId: Long) = placesRepository.remove(placeId)

    private fun selectPlace(place: Place) {
        selectedPlaceRepo.set(place)
        closeDrawerChannel.offer(Unit)
    }

    private fun showRemovePlaceDialog(place: Place.Custom) {
        val dialogData = RemovePlaceDialogData(
            TextRef.stringRes(R.string.remove_thing, place.name),
            place.id
        )
        openRemovePlaceDialogChannel.offer(dialogData)
    }

    private fun navigateToAddPlace() {
        closeDrawerChannel.offer(Unit)
        navigateToAddPlaceChannel.offer(Unit)
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

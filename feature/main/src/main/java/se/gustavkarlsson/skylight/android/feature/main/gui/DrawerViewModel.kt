package se.gustavkarlsson.skylight.android.feature.main.gui

import se.gustavkarlsson.skylight.android.lib.ui.compose.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.ui.graphics.vector.ImageVector
import com.ioki.textref.TextRef
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import se.gustavkarlsson.skylight.android.feature.main.R
import se.gustavkarlsson.skylight.android.lib.places.Place
import se.gustavkarlsson.skylight.android.lib.places.PlacesRepository
import se.gustavkarlsson.skylight.android.lib.places.SelectedPlaceRepository
import se.gustavkarlsson.skylight.android.lib.ui.CoroutineScopedService

// FIXME merge with main view model?
@ExperimentalCoroutinesApi
@FlowPreview
internal class DrawerViewModel @Inject constructor(
    private val placesRepository: PlacesRepository,
    private val selectedPlaceRepo: SelectedPlaceRepository
) : CoroutineScopedService() {
    val drawerItems: Flow<List<DrawerItem>> =
        combine(
            placesRepository.stream(),
            selectedPlaceRepo.stream()
        ) { places, selectedPlace ->
            createPlaceItems(places, selectedPlace)
        }

    private fun createPlaceItems(places: List<Place>, selected: Place) =
        places.map { place ->
            DrawerItem(
                isActive = place == selected,
                icon = createIcon(place),
                text = place.name,
                clickEvent = DrawerClickEvent.PlaceClicked(place),
                longClickEvent = createLongClickEvent(place)
            )
        } + createAddPlaceItem()

    private fun createIcon(place: Place) =
        when (place) {
            is Place.Current -> Icons.LocationOn
            is Place.Custom -> Icons.Map
        }

    private fun createLongClickEvent(place: Place) =
        if (place is Place.Custom) {
            DrawerLongClickEvent(place)
        } else null

    private fun createAddPlaceItem() =
        DrawerItem(
            isActive = false,
            icon = Icons.Add,
            text = TextRef.stringRes(R.string.add_place),
            clickEvent = DrawerClickEvent.AddPlaceClicked,
            longClickEvent = null
        )

    fun onEvent(event: DrawerClickEvent.PlaceClicked) {
        selectedPlaceRepo.set(event.place)
    }

    fun onEvent(event: DrawerLongClickEvent) {
        scope.launch {
            placesRepository.remove(event.place.id)
        }
    }
}

internal data class DrawerItem(
    val isActive: Boolean,
    val icon: ImageVector,
    val text: TextRef,
    val clickEvent: DrawerClickEvent?,
    val longClickEvent: DrawerLongClickEvent?
)

internal sealed class DrawerClickEvent {
    data class PlaceClicked(val place: Place) : DrawerClickEvent()
    object AddPlaceClicked : DrawerClickEvent()
}

internal data class DrawerLongClickEvent(val place: Place.Custom)

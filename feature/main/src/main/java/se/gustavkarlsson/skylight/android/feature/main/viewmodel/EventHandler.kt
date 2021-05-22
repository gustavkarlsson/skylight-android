package se.gustavkarlsson.skylight.android.feature.main.viewmodel

import kotlinx.coroutines.channels.SendChannel
import se.gustavkarlsson.conveyor.Store
import se.gustavkarlsson.conveyor.issue
import se.gustavkarlsson.skylight.android.feature.main.state.Search
import se.gustavkarlsson.skylight.android.feature.main.state.State
import se.gustavkarlsson.skylight.android.lib.permissions.PermissionChecker
import se.gustavkarlsson.skylight.android.lib.places.Place
import se.gustavkarlsson.skylight.android.lib.places.PlacesRepository
import se.gustavkarlsson.skylight.android.lib.places.SelectedPlaceRepository
import se.gustavkarlsson.skylight.android.lib.settings.Settings
import se.gustavkarlsson.skylight.android.lib.ui.compose.SearchFieldState
import javax.inject.Inject

internal class EventHandler @Inject constructor(
    private val store: Store<State>,
    private val placesRepository: PlacesRepository,
    private val settings: Settings,
    private val permissionChecker: PermissionChecker,
    private val selectedPlaceRepository: SelectedPlaceRepository,
    private val searchChannel: SendChannel<@JvmSuppressWildcards SearchFieldState>,
) {

    suspend fun onEvent(event: Event) {
        @Suppress("UNUSED_VARIABLE")
        val dummy: Any = when (event) {
            is Event.AddFavorite -> placesRepository.setFavorite(event.place.id)
            is Event.RemoveFavorite -> placesRepository.setRecent(event.place.id)
            is Event.SetNotificationLevel -> settings.setNotificationTriggerLevel(event.place.id, event.level)
            is Event.SearchChanged -> searchChannel.send(event.state)
            is Event.SelectSearchResult -> onSearchResultClicked(event.result)
            Event.RefreshLocationPermission -> permissionChecker.refresh()
            Event.Noop -> Unit
        }
    }

    private suspend fun onSearchResultClicked(result: SearchResult) {
        val place = when (result) {
            is SearchResult.Known.Current -> {
                Place.Current
            }
            is SearchResult.Known.Saved -> {
                placesRepository.updateLastChanged(result.place.id)
            }
            is SearchResult.New -> {
                placesRepository.addRecent(result.name, result.location)
            }
        }
        selectedPlaceRepository.set(place)
        store.issue { state ->
            state.update { copy(search = Search.Inactive) }
        }
    }
}

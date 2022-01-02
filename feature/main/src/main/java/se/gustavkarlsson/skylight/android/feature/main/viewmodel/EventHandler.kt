package se.gustavkarlsson.skylight.android.feature.main.viewmodel

import kotlinx.coroutines.channels.SendChannel
import se.gustavkarlsson.conveyor.Store
import se.gustavkarlsson.conveyor.issue
import se.gustavkarlsson.skylight.android.feature.main.state.Search
import se.gustavkarlsson.skylight.android.feature.main.state.State
import se.gustavkarlsson.skylight.android.lib.permissions.PermissionChecker
import se.gustavkarlsson.skylight.android.lib.places.Place
import se.gustavkarlsson.skylight.android.lib.places.PlaceId
import se.gustavkarlsson.skylight.android.lib.places.PlacesRepository
import se.gustavkarlsson.skylight.android.lib.places.SelectedPlaceRepository
import se.gustavkarlsson.skylight.android.lib.settings.SettingsRepository
import se.gustavkarlsson.skylight.android.lib.ui.compose.SearchFieldState
import javax.inject.Inject

internal class EventHandler @Inject constructor(
    private val store: Store<State>,
    private val placesRepository: PlacesRepository,
    private val settingsRepository: SettingsRepository,
    private val permissionChecker: PermissionChecker,
    private val selectedPlaceRepository: SelectedPlaceRepository,
    private val searchChannel: SendChannel<@JvmSuppressWildcards SearchFieldState>,
) {

    suspend fun onEvent(event: Event) {
        when (event) {
            is Event.SetNotifications -> {
                settingsRepository.setPlaceNotification(event.place.id, event.enabled)
            }
            is Event.SearchChanged -> {
                searchChannel.send(event.state)
            }
            is Event.ClickSearchResult -> {
                onSearchResultClicked(event.result)
            }
            is Event.LongClickSearchResult -> {
                onSearchResultLongClicked(event.result)
            }
            Event.RefreshLocationPermission -> {
                permissionChecker.refresh()
            }
            Event.TurnOffCurrentLocationNotifications -> {
                settingsRepository.setPlaceNotification(PlaceId.Current, false)
            }
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
                placesRepository.insert(result.name, result.location)
            }
        }
        selectedPlaceRepository.set(place.id)
        store.issue { stateFlow ->
            stateFlow.update {
                when (this) {
                    is State.Loading -> copy(search = Search.Inactive)
                    is State.Ready -> copy(search = Search.Inactive)
                }
            }
        }
    }

    private suspend fun onSearchResultLongClicked(result: SearchResult) {
        // FIXME show confirm dialog
        if (result is SearchResult.Known.Saved) {
            placesRepository.delete(result.place.id)
        }
    }
}

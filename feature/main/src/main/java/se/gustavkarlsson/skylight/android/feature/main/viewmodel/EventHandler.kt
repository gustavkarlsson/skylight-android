package se.gustavkarlsson.skylight.android.feature.main.viewmodel

import kotlinx.coroutines.channels.SendChannel
import se.gustavkarlsson.conveyor.Store
import se.gustavkarlsson.conveyor.issue
import se.gustavkarlsson.skylight.android.core.entities.TriggerLevel
import se.gustavkarlsson.skylight.android.feature.main.state.Search
import se.gustavkarlsson.skylight.android.feature.main.state.State
import se.gustavkarlsson.skylight.android.lib.permissions.PermissionChecker
import se.gustavkarlsson.skylight.android.lib.places.Place
import se.gustavkarlsson.skylight.android.lib.places.PlaceId
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
            is Event.AddBookmark -> placesRepository.addBookmark(event.place.id)
            is Event.RemoveBookmark -> {
                settings.setNotificationTriggerLevel(event.place.id, TriggerLevel.NEVER)
                placesRepository.removeBookmark(event.place.id)
            }
            is Event.SetNotificationLevel -> {
                if (event.place is Place.Saved && !event.place.bookmarked) {
                    if (event.level != TriggerLevel.NEVER) {
                        placesRepository.addBookmark(event.place.id)
                    }
                }
                settings.setNotificationTriggerLevel(event.place.id, event.level)
            }
            is Event.SearchChanged -> searchChannel.send(event.state)
            is Event.SelectSearchResult -> onSearchResultClicked(event.result)
            Event.RefreshLocationPermission -> permissionChecker.refresh()
            Event.TurnOffCurrentLocationNotifications -> {
                settings.setNotificationTriggerLevel(PlaceId.Current, TriggerLevel.NEVER)
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
}

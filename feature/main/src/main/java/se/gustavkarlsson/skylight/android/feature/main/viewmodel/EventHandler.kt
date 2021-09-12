package se.gustavkarlsson.skylight.android.feature.main.viewmodel

import app.cash.exhaustive.Exhaustive
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.flow.first
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

private const val SAVED_PLACES_TO_KEEP = 3

internal class EventHandler @Inject constructor(
    private val store: Store<State>,
    private val placesRepository: PlacesRepository,
    private val settingsRepository: SettingsRepository,
    private val permissionChecker: PermissionChecker,
    private val selectedPlaceRepository: SelectedPlaceRepository,
    private val searchChannel: SendChannel<@JvmSuppressWildcards SearchFieldState>,
) {

    suspend fun onEvent(event: Event) {
        @Exhaustive
        when (event) {
            is Event.SetNotifications -> {
                settingsRepository.setPlaceNotification(event.place.id, event.enabled)
                if (!event.enabled) {
                    trimPlaces()
                }
            }
            is Event.SearchChanged -> searchChannel.send(event.state)
            is Event.SelectSearchResult -> onSearchResultClicked(event.result)
            Event.RefreshLocationPermission -> permissionChecker.refresh()
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
                placesRepository.insert(result.name, result.location).also {
                    trimPlaces()
                }
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

    private suspend fun trimPlaces() {
        val allPlaces = placesRepository.stream().first()
        val savedPlaces = allPlaces.filterIsInstance<Place.Saved>()
        if (savedPlaces.size < SAVED_PLACES_TO_KEEP) return
        val settings = settingsRepository.stream().first()
        val placeIdsToDelete = savedPlaces
            .filter { place ->
                place.id !in settings.placeIdsWithNotification
            }
            .sortedByDescending { it.lastChanged }
            .drop(SAVED_PLACES_TO_KEEP)
            .map { it.id }
        for (id in placeIdsToDelete) {
            placesRepository.delete(id)
        }
    }
}

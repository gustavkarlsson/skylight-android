package se.gustavkarlsson.skylight.android.feature.main.viewmodel

import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import se.gustavkarlsson.conveyor.Store
import se.gustavkarlsson.conveyor.issue
import se.gustavkarlsson.skylight.android.feature.main.state.Search
import se.gustavkarlsson.skylight.android.feature.main.state.State
import se.gustavkarlsson.skylight.android.feature.main.state.Suggestions
import se.gustavkarlsson.skylight.android.lib.permissions.PermissionChecker
import se.gustavkarlsson.skylight.android.lib.places.Place
import se.gustavkarlsson.skylight.android.lib.places.PlacesRepository
import se.gustavkarlsson.skylight.android.lib.places.SelectedPlaceRepository
import se.gustavkarlsson.skylight.android.lib.settings.Settings
import se.gustavkarlsson.skylight.android.lib.ui.compose.SearchFieldState

@ExperimentalCoroutinesApi
internal class EventHandler @Inject constructor(
    private val store: Store<State>,
    private val placesRepository: PlacesRepository,
    private val settings: Settings,
    private val permissionChecker: PermissionChecker,
    private val selectedPlaceRepository: SelectedPlaceRepository,
) {

    suspend fun onEvent(event: Event) {
        @Suppress("UNUSED_VARIABLE")
        val dummy: Any = when (event) {
            is Event.AddFavorite -> placesRepository.setFavorite(event.place.id)
            is Event.RemoveFavorite -> placesRepository.setRecent(event.place.id)
            is Event.SetNotificationLevel -> settings.setNotificationTriggerLevel(event.place, event.level)
            is Event.SearchChanged -> onSearchChanged(event.state)
            is Event.SelectSearchResult -> onSearchResultClicked(event.result)
            Event.RefreshLocationPermission -> permissionChecker.refresh()
            Event.Noop -> Unit
        }
    }

    private fun onSearchChanged(searchFieldState: SearchFieldState) = store.issue { state ->
        state.update {
            val search = when (searchFieldState) {
                SearchFieldState.Inactive -> Search.Inactive
                is SearchFieldState.Active -> {
                    val query = searchFieldState.text
                    if (query.isBlank()) {
                        Search.Active.Blank(query)
                    } else {
                        when (search) {
                            Search.Inactive, is Search.Active.Blank -> {
                                Search.Active.Success(query, inProgress = true, Suggestions("", emptyList()))
                            }
                            is Search.Active.Failure -> search.copy(query = query, inProgress = true)
                            is Search.Active.Success -> search.copy(query = query, inProgress = true)
                        }
                    }
                }
            }
            copy(search = search)
        }
    }

    private suspend fun onSearchResultClicked(result: SearchResult) {
        val place = when (result) {
            is SearchResult.Known -> {
                when (result.place) {
                    Place.Current -> result.place
                    is Place.Favorite, is Place.Recent -> placesRepository.updateLastChanged(result.place.id)
                }
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

package se.gustavkarlsson.skylight.android.lib.places

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import se.gustavkarlsson.conveyor.Action
import se.gustavkarlsson.conveyor.Store
import se.gustavkarlsson.conveyor.UpdatableStateFlow
import se.gustavkarlsson.skylight.android.core.logging.logError

@FlowPreview
@ExperimentalCoroutinesApi
internal class PlacesRepoSelectedPlaceRepository(
    placesRepo: PlacesRepository,
    private val placeSelectionStorage: PlaceSelectionStorage,
    scope: CoroutineScope
) : SelectedPlaceRepository {
    @FlowPreview
    private val store = Store(
        initialState = State.Initial,
        startActions = listOf(StreamPlacesAction(placeSelectionStorage::loadId, placesRepo.stream()))
    )

    init {
        store.start(scope)
    }

    override fun set(place: Place) = store.issue(SelectionChangedAction(place, placeSelectionStorage::saveId))

    override fun get(): Place = store.state.value.selected

    override fun stream(): Flow<Place> = store.state
        .map { it.selected }
        .distinctUntilChanged()
}

private sealed class State {
    abstract val selected: Place

    // TODO Make get() suspending and don't use this default
    object Initial : State() {
        override val selected = Place.Current
    }

    data class Loaded(override val selected: Place, val places: List<Place>) : State()
}

private class StreamPlacesAction(
    private val loadId: suspend () -> PlaceId,
    private val placesStream: Flow<List<Place>>,
) : Action<State> {
    override suspend fun execute(state: UpdatableStateFlow<State>) {
        val initialSelectedId = loadId()
        placesStream
            .collect { newPlaces ->
                state.update {
                    createState(this, newPlaces, initialSelectedId)
                }
            }
    }

    private fun createState(state: State, newPlaces: List<Place>, initialSelectedId: PlaceId): State {
        val selected = when (state) {
            is State.Initial -> {
                val placeMatchingId = newPlaces.firstOrNull { place -> place.id == initialSelectedId }
                placeMatchingId ?: newPlaces.first()
            }
            is State.Loaded -> {
                if (state.selected.id in newPlaces.ids()) {
                    state.selected
                } else newPlaces.first()
            }
        }
        return State.Loaded(selected = selected, places = newPlaces)
    }
}

private class SelectionChangedAction(
    private val selectedPlace: Place,
    private val saveId: (PlaceId) -> Unit,
) : Action<State> {
    override suspend fun execute(state: UpdatableStateFlow<State>) {
        val newState = state.update {
            when (this) {
                is State.Initial -> {
                    logError { "Cannot select a place before loading places. Place: $selectedPlace" }
                    this
                }
                is State.Loaded -> {
                    if (selectedPlace.id in places.ids()) {
                        copy(selected = selectedPlace)
                    } else {
                        logError { "Cannot select a place that is not loaded. Place: $selectedPlace, Loaded: $places" }
                        this
                    }
                }
            }
        }
        if (newState is State.Loaded) {
            saveId(selectedPlace.id)
        }
    }
}

private fun List<Place>.ids(): List<PlaceId> = map { it.id }

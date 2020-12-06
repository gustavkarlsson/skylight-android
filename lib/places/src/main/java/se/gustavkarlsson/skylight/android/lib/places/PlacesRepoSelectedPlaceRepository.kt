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
        startActions = listOf(StreamPlacesAction(placeSelectionStorage::loadIndex, placesRepo.stream()))
    )

    init { store.start(scope) }

    override fun set(place: Place) = store.issue(SelectionChangedAction(place, placeSelectionStorage::saveIndex))

    override fun get(): Place = store.state.value.selected

    override fun stream(): Flow<Place> = store.state
        .map { it.selected }
        .distinctUntilChanged()
}

private sealed class State {
    abstract val selected: Place

    object Initial : State() {
        override val selected = Place.Current
    }

    data class Loaded(override val selected: Place, val places: List<Place>) : State()
}

private class StreamPlacesAction(
    private val loadIndex: suspend () -> Int?,
    private val placesStream: Flow<List<Place>>,
) : Action<State> {
    override suspend fun execute(state: UpdatableStateFlow<State>) {
        val initialSelectedIndex = loadIndex()
        placesStream
            .collect { newPlaces ->
                state.update {
                    createNewState(initialSelectedIndex, this, newPlaces)
                }
            }
    }

    private fun createNewState(initialSelectedIndex: Int?, state: State, newPlaces: List<Place>): State {
        val selected = when (state) {
            is State.Initial -> {
                if (initialSelectedIndex != null) {
                    val index = initialSelectedIndex.coerceIn(newPlaces.indices)
                    newPlaces[index]
                } else newPlaces.last()
            }
            is State.Loaded -> when {
                newPlaces.size > state.places.size -> {
                    (newPlaces - state.places).first()
                }
                state.selected in newPlaces -> {
                    state.selected
                }
                else -> {
                    val newIndex = state.places.indexOf(state.selected).coerceIn(newPlaces.indices)
                    newPlaces[newIndex]
                }
            }
        }
        return State.Loaded(selected = selected, places = newPlaces)
    }
}

private class SelectionChangedAction(
    private val selectedPlace: Place,
    private val saveIndex: (Int) -> Unit,
) : Action<State> {
    override suspend fun execute(state: UpdatableStateFlow<State>) {
        val newState = state.update {
            when (this) {
                is State.Initial -> {
                    logError { "Cannot select a place before loading places. Place: $selectedPlace" }
                    this
                }
                is State.Loaded -> {
                    if (selectedPlace in places) {
                        copy(selected = selectedPlace)
                    } else {
                        logError { "Cannot select a place that is not loaded. Place: $selectedPlace, Loaded: $places" }
                        this
                    }
                }
            }
        }
        if (newState is State.Loaded) {
            val index = newState.places.indexOf(newState.selected)
            saveIndex(index)
        }
    }
}

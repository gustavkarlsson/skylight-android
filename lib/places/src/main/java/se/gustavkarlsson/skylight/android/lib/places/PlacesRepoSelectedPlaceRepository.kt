package se.gustavkarlsson.skylight.android.lib.places

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import se.gustavkarlsson.conveyor.Action
import se.gustavkarlsson.conveyor.StateAccess
import se.gustavkarlsson.conveyor.buildStore
import se.gustavkarlsson.skylight.android.core.logging.logError

@FlowPreview
@ExperimentalCoroutinesApi
internal class PlacesRepoSelectedPlaceRepository(
    placesRepo: PlacesRepository,
    placeSelectionStorage: PlaceSelectionStorage,
    scope: CoroutineScope
) : SelectedPlaceRepository {
    @FlowPreview
    private val store = buildStore(
        initialState = State.Initial,
        openActions = listOf(StreamPlacesAction(placeSelectionStorage::loadIndex, placesRepo.stream()))
    )

    init {
        store.start(scope)
        scope.launch { continuouslySaveSelectedPlace(placeSelectionStorage::saveIndex) }
    }

    private suspend fun continuouslySaveSelectedPlace(saveIndex: (Int) -> Unit) =
        store.state // TODO Replace with store watcher when available
            .filterIsInstance<State.Loaded>()
            .collect { state ->
                val index = state.places.indexOf(state.selected)
                saveIndex(index)
            }

    override fun set(place: Place) = store.issue(SelectionChangedAction(place))

    override fun get(): Place = store.currentState.selected

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
    override suspend fun execute(stateAccess: StateAccess<State>) {
        val initialSelectedIndex = loadIndex()
        placesStream
            .collect { newPlaces ->
                stateAccess.update { state ->
                    createNewState(initialSelectedIndex, state, newPlaces)
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

private data class SelectionChangedAction(val selectedPlace: Place) : Action<State> {
    override suspend fun execute(stateAccess: StateAccess<State>) {
        stateAccess.update { state ->
            when (state) {
                is State.Initial -> {
                    logError { "Cannot select a place before loading places. Place: $selectedPlace" }
                    state
                }
                is State.Loaded -> {
                    if (selectedPlace in state.places) {
                        state.copy(selected = selectedPlace)
                    } else {
                        logError { "Cannot select a place that is not loaded. Place: $selectedPlace, Loaded: ${state.places}" }
                        state
                    }
                }
            }
        }
    }
}

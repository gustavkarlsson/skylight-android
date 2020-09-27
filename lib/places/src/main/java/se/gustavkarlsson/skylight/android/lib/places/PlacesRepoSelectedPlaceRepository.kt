package se.gustavkarlsson.skylight.android.lib.places

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import se.gustavkarlsson.conveyor.Change
import se.gustavkarlsson.conveyor.Command
import se.gustavkarlsson.conveyor.actions.FlowAction
import se.gustavkarlsson.conveyor.buildStore
import se.gustavkarlsson.conveyor.only
import se.gustavkarlsson.skylight.android.core.logging.logError
import se.gustavkarlsson.skylight.android.core.utils.allowDiskReadsInStrictMode

@ExperimentalCoroutinesApi
internal class PlacesRepoSelectedPlaceRepository(
    placesRepo: PlacesRepository,
    placeSelectionStorage: PlaceSelectionStorage,
    scope: CoroutineScope
) : SelectedPlaceRepository {
    private val store = buildStore(
        initialState = allowDiskReadsInStrictMode {
            // TODO This should not run on the main thread
            State.Initial(placeSelectionStorage.loadIndex())
        },
        openActions = listOf(StreamPlacesAction(placesRepo.stream()))
    )

    init {
        store.open(scope)
        // TODO Replace with store watcher when available
        scope.launch {
            store.state
                .filterIsInstance<State.Loaded>()
                .collect { state ->
                    val index = state.places.indexOf(state.selected)
                    placeSelectionStorage.saveIndex(index)
                }
        }
    }

    override fun set(place: Place) = store.issue(SelectionChangedCommand(place))

    override fun get(): Place = store.currentState.selected

    override fun stream(): Flow<Place> = store.state
        .map { it.selected }
        .distinctUntilChanged()
}

private sealed class State {
    abstract val selected: Place

    data class Initial(val selectedIndex: Int?) : State() {
        override val selected = Place.Current
    }

    data class Loaded(override val selected: Place, val places: List<Place>) : State()
}

private class StreamPlacesAction(placesStream: Flow<List<Place>>) : FlowAction<State>() {
    override val flow: Flow<Command<State>> =
        placesStream.map { PlacesUpdatedCommand(it) }
}

private data class PlacesUpdatedCommand(private val newPlaces: List<Place>) : Command<State> {
    override fun reduce(state: State): Change<State> {
        val selected = when (state) {
            is State.Initial -> {
                if (state.selectedIndex != null) {
                    val index = state.selectedIndex.coerceIn(newPlaces.indices)
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
        return State.Loaded(selected = selected, places = newPlaces).only()
    }
}

private data class SelectionChangedCommand(val selectedPlace: Place) : Command<State> {
    override fun reduce(state: State): Change<State> {
        val newState = when (state) {
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
        return newState.only()
    }
}

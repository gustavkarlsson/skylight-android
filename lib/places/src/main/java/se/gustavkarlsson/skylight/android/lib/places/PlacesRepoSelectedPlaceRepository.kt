package se.gustavkarlsson.skylight.android.lib.places

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import se.gustavkarlsson.conveyor.Action
import se.gustavkarlsson.conveyor.AtomicStateFlow
import se.gustavkarlsson.conveyor.Store

internal class PlacesRepoSelectedPlaceRepository(
    placesRepo: PlacesRepository,
    private val placeSelectionStorage: PlaceSelectionStorage,
    scope: CoroutineScope
) : SelectedPlaceRepository {
    private val store = Store(
        initialState = State.Loading(selectedId = null),
        startActions = listOf(StreamPlacesAction(placeSelectionStorage::loadId, placesRepo.stream()))
    )

    init {
        store.start(scope)
    }

    override fun set(placeId: PlaceId) = store.issue(SelectionChangedAction(placeId, placeSelectionStorage::saveId))

    override fun stream(): Flow<Place> = store.state
        .filterIsInstance<State.Loaded>()
        .map { state ->
            state.places
                .firstOrNull { place ->
                    place.id == state.selectedId
                } ?: state.places.first()
        }
        .distinctUntilChanged()
}

private sealed interface State {
    val selectedId: PlaceId?

    data class Loading(override val selectedId: PlaceId?) : State
    data class Loaded(override val selectedId: PlaceId, val places: List<Place>) : State
}

private class StreamPlacesAction(
    private val loadId: suspend () -> PlaceId,
    private val placesStream: Flow<List<Place>>,
) : Action<State> {
    override suspend fun execute(stateFlow: AtomicStateFlow<State>) {
        val initialSelectedId = loadId()
        placesStream
            .collect { newPlaces ->
                stateFlow.update {
                    State.Loaded(
                        selectedId = selectedId ?: initialSelectedId,
                        places = newPlaces,
                    )
                }
            }
    }
}

private class SelectionChangedAction(
    private val placeId: PlaceId,
    private val saveId: (PlaceId) -> Unit,
) : Action<State> {
    override suspend fun execute(stateFlow: AtomicStateFlow<State>) {
        if (placeId == stateFlow.value.selectedId) return
        stateFlow.update {
            when (this) {
                is State.Loading -> copy(selectedId = placeId)
                is State.Loaded -> copy(selectedId = placeId)
            }
        }
        saveId(placeId)
    }
}

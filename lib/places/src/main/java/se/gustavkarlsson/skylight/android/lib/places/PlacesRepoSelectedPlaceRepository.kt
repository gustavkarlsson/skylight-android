package se.gustavkarlsson.skylight.android.lib.places

import arrow.core.NonEmptyList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import se.gustavkarlsson.conveyor.Action
import se.gustavkarlsson.conveyor.AtomicStateFlow
import se.gustavkarlsson.conveyor.Store
import se.gustavkarlsson.skylight.android.core.AppScope
import se.gustavkarlsson.skylight.android.core.Global
import se.gustavkarlsson.skylight.android.core.logging.logInfo
import se.gustavkarlsson.skylight.android.core.logging.logWarn
import javax.inject.Inject

@AppScope
internal class PlacesRepoSelectedPlaceRepository @Inject constructor(
    placesRepo: PlacesRepository,
    private val placeSelectionStorage: PlaceSelectionStorage,
    @Global scope: CoroutineScope,
) : SelectedPlaceRepository {
    private val store = Store(
        initialState = State.Loading(suggestedId = null),
        startActions = listOf(StreamPlacesAction(placeSelectionStorage::loadId, placesRepo.stream())),
    )

    init {
        store.start(scope)
    }

    override fun set(placeId: PlaceId) = store.issue(SelectionChangedAction(placeId, placeSelectionStorage::saveId))

    override fun stream(): Flow<Place> = store.state
        .filterIsInstance<State.Loaded>()
        .map { state ->
            state.places.first { place ->
                place.id == state.selectedId
            }
        }
        .distinctUntilChanged()
}

private sealed interface State {
    data class Loading(val suggestedId: PlaceId?) : State
    data class Loaded(val selectedId: PlaceId, val places: NonEmptyList<Place>) : State
}

private class StreamPlacesAction(
    private val loadId: suspend () -> PlaceId,
    private val placesStream: Flow<NonEmptyList<Place>>,
) : Action<State> {
    override suspend fun execute(stateFlow: AtomicStateFlow<State>) {
        val initialSelectedId = loadId()
        placesStream.collect { newPlaces ->
            stateFlow.update {
                val newSelectedId = selectBestId(this, initialSelectedId, newPlaces)
                logInfo { "Places updated to $newPlaces. New selected Place ID is: $newSelectedId" }
                State.Loaded(
                    selectedId = newSelectedId,
                    places = newPlaces,
                )
            }
        }
    }
}

private fun selectBestId(
    state: State,
    initialSelectedId: PlaceId,
    newPlaces: NonEmptyList<Place>,
): PlaceId {
    val selectedIdCandidates = listOfNotNull(
        when (state) {
            is State.Loading -> state.suggestedId
            is State.Loaded -> state.selectedId
        },
        initialSelectedId,
    )
    return selectedIdCandidates.firstOrNull { placeId ->
        placeId in newPlaces.ids()
    } ?: newPlaces.ids().first()
}

private class SelectionChangedAction(
    private val placeId: PlaceId,
    private val saveId: (PlaceId) -> Unit,
) : Action<State> {
    override suspend fun execute(stateFlow: AtomicStateFlow<State>) {
        var save = false
        stateFlow.update {
            when (this) {
                is State.Loading -> copy(suggestedId = placeId)
                is State.Loaded -> {
                    when (placeId) {
                        selectedId -> {
                            logInfo { "Place ID: $placeId already selected" }
                            this
                        }
                        !in places.ids() -> {
                            logWarn { "Place with ID: $placeId does not exist. Keep selected place ID:  $selectedId" }
                            this
                        }
                        else -> {
                            logInfo { "Selecting new Place ID: $placeId" }
                            save = true
                            copy(selectedId = placeId)
                        }
                    }
                }
            }
        }
        if (save) {
            saveId(placeId)
        }
    }
}

private fun NonEmptyList<Place>.ids() = map { it.id }

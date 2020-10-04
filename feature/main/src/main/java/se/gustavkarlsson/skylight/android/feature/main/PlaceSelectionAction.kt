package se.gustavkarlsson.skylight.android.feature.main

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import se.gustavkarlsson.conveyor.Action
import se.gustavkarlsson.conveyor.StateAccess
import se.gustavkarlsson.skylight.android.lib.aurora.LoadableAuroraReport
import se.gustavkarlsson.skylight.android.lib.places.Place

internal class PlaceSelectionAction(
    private val places: Flow<Place>,
    private val toggleStreamAction: ToggleStreamAction,
) : Action<State> {
    override suspend fun execute(stateAccess: StateAccess<State>) {
        places.collect { place ->
            stateAccess.update { state ->
                state.copy(
                    selectedPlace = place,
                    selectedAuroraReport = LoadableAuroraReport.LOADING // TODO Get from cache?
                )
            }
            toggleStreamAction.execute(stateAccess)
        }
    }
}

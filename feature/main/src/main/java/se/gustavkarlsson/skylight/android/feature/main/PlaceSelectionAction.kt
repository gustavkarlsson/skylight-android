package se.gustavkarlsson.skylight.android.feature.main

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import se.gustavkarlsson.conveyor.Action
import se.gustavkarlsson.conveyor.UpdateState
import se.gustavkarlsson.skylight.android.lib.aurora.LoadableAuroraReport
import se.gustavkarlsson.skylight.android.lib.places.Place

internal class PlaceSelectionAction(private val places: Flow<Place>) : Action<State> {
    override suspend fun execute(updateState: UpdateState<State>) {
        places.collect { place ->
            updateState { state ->
                state.copy(
                    selectedPlace = place,
                    selectedAuroraReport = LoadableAuroraReport.LOADING // TODO Get from cache?
                )
            }
            TODO("FIXMEToggle streaming?")
        }
    }
}

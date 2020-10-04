package se.gustavkarlsson.skylight.android.feature.main

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import se.gustavkarlsson.conveyor.Action
import se.gustavkarlsson.conveyor.StateAccess
import se.gustavkarlsson.skylight.android.core.entities.Loadable
import se.gustavkarlsson.skylight.android.lib.aurora.LoadableAuroraReport
import se.gustavkarlsson.skylight.android.lib.location.LocationResult
import se.gustavkarlsson.skylight.android.lib.places.Place

internal class ToggleStreamAction(
    private val currentLocation: Flow<Loadable<LocationResult>>,
    private val streamAuroraReports: (Flow<Loadable<LocationResult>>) -> Flow<LoadableAuroraReport>
) : Action<State> {
    override suspend fun execute(stateAccess: StateAccess<State>) {
        val scope = CoroutineScope(Job())
        val newState = stateAccess.update { state ->
            state.streamScope?.cancel("Streaming toggled")
            state.copy(streamScope = scope)
        }

        val selectedPlace = newState.selectedPlace
        val locationUpdates = if (selectedPlace is Place.Custom) {
            flowOf(Loadable.loaded(LocationResult.success(selectedPlace.location)))
        } else {
            currentLocation
        }
        val reportStream = streamAuroraReports(locationUpdates)
        scope.launch {
            reportStream
                .collect { report ->
                    stateAccess.update { state ->
                        if (state.selectedPlace == selectedPlace) {
                            state.copy(selectedAuroraReport = report)
                        } else {
                            state
                        }
                    }
                }
        }
    }
}

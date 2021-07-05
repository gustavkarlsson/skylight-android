package se.gustavkarlsson.skylight.android.feature.main.state

import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filterIsInstance
import se.gustavkarlsson.conveyor.Action
import se.gustavkarlsson.conveyor.AtomicStateFlow
import javax.inject.Inject

internal class FinishLoadingAction @Inject constructor() : Action<State> {
    override suspend fun execute(stateFlow: AtomicStateFlow<State>) {
        stateFlow
            .filterIsInstance<State.Loading>()
            .collect { loadingState ->
                val selectedPlaceId = loadingState.selectedPlaceId
                val places = loadingState.places
                val notificationTriggerLevels = loadingState.notificationTriggerLevels
                if (selectedPlaceId != null && places != null && notificationTriggerLevels != null) {
                    // FIXME This sometimes locks
                    stateFlow.update {
                        State.Ready(
                            permissions = permissions,
                            currentLocationName = currentLocationName,
                            selectedPlaceId = selectedPlaceId,
                            selectedAuroraReport = selectedAuroraReport,
                            search = search,
                            places = places,
                            notificationTriggerLevels = notificationTriggerLevels,
                        )
                    }
                }
            }
    }
}

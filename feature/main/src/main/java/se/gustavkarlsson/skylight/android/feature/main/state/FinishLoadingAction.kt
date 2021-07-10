package se.gustavkarlsson.skylight.android.feature.main.state

import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterIsInstance
import se.gustavkarlsson.conveyor.Action
import se.gustavkarlsson.conveyor.AtomicStateFlow
import javax.inject.Inject

internal class FinishLoadingAction @Inject constructor() : Action<State> {
    override suspend fun execute(stateFlow: AtomicStateFlow<State>) {
        stateFlow
            .filterIsInstance<State.Loading>()
            .collectLatest { loadingState ->
                val selectedPlace = loadingState.selectedPlace
                val places = loadingState.places
                val notificationTriggerLevels = loadingState.notificationTriggerLevels
                if (selectedPlace != null && places != null && notificationTriggerLevels != null) {
                    stateFlow.update {
                        State.Ready(
                            permissions = permissions,
                            currentLocationName = currentLocationName,
                            selectedPlace = selectedPlace,
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

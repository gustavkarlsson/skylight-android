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
            .collectLatest {
                stateFlow.update {
                    when (this) {
                        is State.Loading -> updateLoading()
                        is State.Ready -> this
                    }
                }
            }
    }
}

private fun State.Loading.updateLoading(): State {
    return if (locationServiceStatus != null && selectedPlace != null && places != null && settings != null) {
        State.Ready(
            permissions = permissions,
            locationServiceStatus = locationServiceStatus,
            currentLocation = currentLocation,
            currentLocationName = currentLocationName,
            selectedPlace = selectedPlace,
            selectedAuroraReport = selectedAuroraReport,
            search = search,
            places = places,
            settings = settings,
            placeToDelete = null,
        )
    } else this
}

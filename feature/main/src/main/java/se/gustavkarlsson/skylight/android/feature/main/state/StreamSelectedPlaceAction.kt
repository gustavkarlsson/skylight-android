package se.gustavkarlsson.skylight.android.feature.main.state

import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import se.gustavkarlsson.conveyor.Action
import se.gustavkarlsson.conveyor.AtomicStateFlow
import se.gustavkarlsson.skylight.android.core.entities.Loading
import se.gustavkarlsson.skylight.android.lib.aurora.LoadableAuroraReport
import se.gustavkarlsson.skylight.android.lib.places.SelectedPlaceRepository
import se.gustavkarlsson.skylight.android.lib.places.savedLocation
import javax.inject.Inject

internal class StreamSelectedPlaceAction @Inject constructor(
    private val selectedPlaceRepository: SelectedPlaceRepository,
) : Action<State> {
    override suspend fun execute(stateFlow: AtomicStateFlow<State>) {
        selectedPlaceRepository.stream()
            .distinctUntilChanged()
            .collectLatest { selectedPlace ->
                stateFlow.update {
                    // TODO Get from cache?
                    // TODO Do we need to update these?
                    val newReport = if (selectedPlace.savedLocation == this.selectedPlace?.savedLocation) {
                        selectedAuroraReport
                    } else {
                        LoadableAuroraReport.LOADING // TODO Get from cache?
                    }
                    val newForecast = if (selectedPlace.savedLocation == this.selectedPlace?.savedLocation) {
                        selectedAuroraForecastReport
                    } else {
                        Loading
                    }
                    when (this) {
                        is State.Loading -> copy(
                            selectedPlace = selectedPlace,
                            selectedAuroraReport = newReport,
                            selectedAuroraForecastReport = newForecast,
                        )
                        is State.Ready -> copy(
                            selectedPlace = selectedPlace,
                            selectedAuroraReport = newReport,
                            selectedAuroraForecastReport = newForecast,
                        )
                    }
                }
            }
    }
}

package se.gustavkarlsson.skylight.android.feature.main.state

import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import se.gustavkarlsson.conveyor.Action
import se.gustavkarlsson.conveyor.AtomicStateFlow
import se.gustavkarlsson.skylight.android.lib.aurora.LoadableAuroraReport
import se.gustavkarlsson.skylight.android.lib.places.SelectedPlaceRepository
import javax.inject.Inject

internal class StreamSelectedPlaceAction @Inject constructor(
    private val selectedPlaceRepository: SelectedPlaceRepository,
) : Action<State> {
    override suspend fun execute(stateFlow: AtomicStateFlow<State>) {
        selectedPlaceRepository.stream()
            .distinctUntilChanged()
            .collectLatest { selectedPlace ->
                stateFlow.update {
                    val loading = LoadableAuroraReport.LOADING // TODO Get from cache?
                    when (this) {
                        is State.Loading -> copy(
                            selectedPlace = selectedPlace,
                            selectedAuroraReport = loading,
                        )
                        is State.Ready -> copy(
                            selectedPlace = selectedPlace,
                            selectedAuroraReport = loading,
                        )
                    }
                }
            }
    }
}

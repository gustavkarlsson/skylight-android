package se.gustavkarlsson.skylight.android.feature.main.state

import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
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
            .map { place -> place.id }
            .distinctUntilChanged()
            .collect { id ->
                stateFlow.update {
                    copy(
                        selectedPlaceId = id,
                        selectedAuroraReport = LoadableAuroraReport.LOADING // TODO Get from cache?
                    )
                }
            }
    }
}

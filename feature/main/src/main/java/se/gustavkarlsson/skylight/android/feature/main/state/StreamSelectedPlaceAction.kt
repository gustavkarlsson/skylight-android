package se.gustavkarlsson.skylight.android.feature.main.state

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import se.gustavkarlsson.conveyor.Action
import se.gustavkarlsson.conveyor.UpdatableStateFlow
import se.gustavkarlsson.skylight.android.lib.aurora.LoadableAuroraReport
import se.gustavkarlsson.skylight.android.lib.places.Place

internal class StreamSelectedPlaceAction(
    private val selectedPlace: Flow<Place>,
) : Action<State> {
    override suspend fun execute(state: UpdatableStateFlow<State>) {
        selectedPlace
            .map { place -> place.id }
            .distinctUntilChanged()
            .collect { id ->
                state.update {
                    copy(
                        selectedPlaceId = id,
                        selectedAuroraReport = LoadableAuroraReport.LOADING // TODO Get from cache?
                    )
                }
            }
    }
}

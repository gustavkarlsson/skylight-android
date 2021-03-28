package se.gustavkarlsson.skylight.android.feature.main

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import se.gustavkarlsson.conveyor.Action
import se.gustavkarlsson.conveyor.UpdatableStateFlow
import se.gustavkarlsson.skylight.android.lib.aurora.LoadableAuroraReport
import se.gustavkarlsson.skylight.android.lib.places.Place

internal class PlaceSelectionAction(
    private val places: Flow<Place>,
) : Action<State> {
    override suspend fun execute(state: UpdatableStateFlow<State>) {
        places.collect { place ->
            state.update {
                copy(
                    selectedPlaceId = place.id,
                    selectedAuroraReport = LoadableAuroraReport.LOADING // TODO Get from cache?
                )
            }
        }
    }
}

package se.gustavkarlsson.skylight.android.feature.main

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import se.gustavkarlsson.conveyor.Action
import se.gustavkarlsson.conveyor.UpdatableStateFlow
import se.gustavkarlsson.skylight.android.lib.places.Place

internal class StreamPlacesAction(
    private val places: Flow<List<Place>>,
) : Action<State> {
    override suspend fun execute(state: UpdatableStateFlow<State>) {
        places.collect { places ->
            state.update {
                copy(places = places)
            }
        }
    }
}

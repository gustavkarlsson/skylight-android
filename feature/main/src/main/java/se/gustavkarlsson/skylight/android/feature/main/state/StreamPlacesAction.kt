package se.gustavkarlsson.skylight.android.feature.main.state

import javax.inject.Inject
import kotlinx.coroutines.flow.collect
import se.gustavkarlsson.conveyor.Action
import se.gustavkarlsson.conveyor.UpdatableStateFlow
import se.gustavkarlsson.skylight.android.lib.places.PlacesRepository

internal class StreamPlacesAction @Inject constructor(
    private val placesRepository: PlacesRepository,
) : Action<State> {
    override suspend fun execute(state: UpdatableStateFlow<State>) {
        placesRepository.stream().collect { places ->
            state.update {
                copy(places = places)
            }
        }
    }
}
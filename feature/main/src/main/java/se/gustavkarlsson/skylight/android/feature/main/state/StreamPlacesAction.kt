package se.gustavkarlsson.skylight.android.feature.main.state

import kotlinx.coroutines.flow.collect
import se.gustavkarlsson.conveyor.Action
import se.gustavkarlsson.conveyor.AtomicStateFlow
import se.gustavkarlsson.skylight.android.lib.places.PlacesRepository
import javax.inject.Inject

internal class StreamPlacesAction @Inject constructor(
    private val placesRepository: PlacesRepository,
) : Action<State> {
    override suspend fun execute(stateFlow: AtomicStateFlow<State>) {
        placesRepository.stream().collect { places ->
            stateFlow.update {
                copy(places = places)
            }
        }
    }
}

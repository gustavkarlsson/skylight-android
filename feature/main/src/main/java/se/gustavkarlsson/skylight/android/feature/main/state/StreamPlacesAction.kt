package se.gustavkarlsson.skylight.android.feature.main.state

import kotlinx.coroutines.flow.collectLatest
import se.gustavkarlsson.conveyor.Action
import se.gustavkarlsson.conveyor.AtomicStateFlow
import se.gustavkarlsson.skylight.android.lib.places.PlacesRepository
import javax.inject.Inject

internal class StreamPlacesAction @Inject constructor(
    private val placesRepository: PlacesRepository,
) : Action<State> {
    override suspend fun execute(stateFlow: AtomicStateFlow<State>) {
        placesRepository.stream().collectLatest { places ->
            stateFlow.update {
                when (this) {
                    is State.Loading -> copy(places = places)
                    is State.Ready -> copy(places = places)
                }
            }
        }
    }
}

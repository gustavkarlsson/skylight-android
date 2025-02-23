package se.gustavkarlsson.skylight.android.feature.main.state

import kotlinx.coroutines.flow.collectLatest
import me.tatarka.inject.annotations.Inject
import se.gustavkarlsson.conveyor.Action
import se.gustavkarlsson.conveyor.AtomicStateFlow
import se.gustavkarlsson.skylight.android.lib.places.PlacesRepository

@Inject
internal class StreamPlacesAction(
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

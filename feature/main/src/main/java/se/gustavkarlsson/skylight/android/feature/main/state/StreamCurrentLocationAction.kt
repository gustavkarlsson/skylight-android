package se.gustavkarlsson.skylight.android.feature.main.state

import kotlinx.coroutines.flow.collectLatest
import me.tatarka.inject.annotations.Inject
import se.gustavkarlsson.conveyor.Action
import se.gustavkarlsson.conveyor.AtomicStateFlow
import se.gustavkarlsson.skylight.android.lib.location.LocationProvider

@Inject
internal class StreamCurrentLocationAction(
    private val locationProvider: LocationProvider,
) : Action<State> {
    override suspend fun execute(stateFlow: AtomicStateFlow<State>) {
        locationProvider.stream().collectLatest { location ->
            stateFlow.update {
                when (this) {
                    is State.Loading -> copy(currentLocation = location)
                    is State.Ready -> copy(currentLocation = location)
                }
            }
        }
    }
}

package se.gustavkarlsson.skylight.android.feature.main.state

import kotlinx.coroutines.flow.collectLatest
import me.tatarka.inject.annotations.Inject
import se.gustavkarlsson.conveyor.Action
import se.gustavkarlsson.conveyor.AtomicStateFlow
import se.gustavkarlsson.skylight.android.lib.location.LocationServiceStatusProvider

@Inject
internal class LocationServiceStatusAction(
    private val locationServiceStatusProvider: LocationServiceStatusProvider,
) : Action<State> {
    override suspend fun execute(stateFlow: AtomicStateFlow<State>) {
        locationServiceStatusProvider.locationServiceStatus.collectLatest { locationServiceStatus ->
            stateFlow.update {
                when (this) {
                    is State.Loading -> copy(locationServiceStatus = locationServiceStatus)
                    is State.Ready -> copy(locationServiceStatus = locationServiceStatus)
                }
            }
        }
    }
}

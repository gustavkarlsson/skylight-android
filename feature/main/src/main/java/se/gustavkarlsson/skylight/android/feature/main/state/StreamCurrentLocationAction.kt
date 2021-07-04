package se.gustavkarlsson.skylight.android.feature.main.state

import kotlinx.coroutines.flow.collect
import se.gustavkarlsson.conveyor.Action
import se.gustavkarlsson.conveyor.AtomicStateFlow
import se.gustavkarlsson.skylight.android.lib.location.LocationProvider
import se.gustavkarlsson.skylight.android.lib.reversegeocoder.ReverseGeocoder
import javax.inject.Inject

internal class StreamCurrentLocationAction @Inject constructor(
    private val locationProvider: LocationProvider,
    private val reverseGeocoder: ReverseGeocoder,
) : Action<State> {
    override suspend fun execute(stateFlow: AtomicStateFlow<State>) {
        reverseGeocoder.stream(locationProvider.stream())
            .collect { loadableGeocodingResult ->
                stateFlow.update {
                    when (this) {
                        is State.Loading -> copy(currentLocationName = loadableGeocodingResult)
                        is State.Ready -> copy(currentLocationName = loadableGeocodingResult)
                    }
                }
            }
    }
}

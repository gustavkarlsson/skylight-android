package se.gustavkarlsson.skylight.android.feature.main.state

import kotlinx.coroutines.flow.collect
import se.gustavkarlsson.conveyor.Action
import se.gustavkarlsson.conveyor.UpdatableStateFlow
import se.gustavkarlsson.skylight.android.lib.location.LocationProvider
import se.gustavkarlsson.skylight.android.lib.reversegeocoder.ReverseGeocoder
import javax.inject.Inject

internal class StreamCurrentLocationAction @Inject constructor(
    private val locationProvider: LocationProvider,
    private val reverseGeocoder: ReverseGeocoder,
) : Action<State> {
    override suspend fun execute(state: UpdatableStateFlow<State>) {
        reverseGeocoder.stream(locationProvider.stream())
            .collect { loadableGeocodingResult ->
                state.update {
                    copy(currentLocationName = loadableGeocodingResult)
                }
            }
    }
}

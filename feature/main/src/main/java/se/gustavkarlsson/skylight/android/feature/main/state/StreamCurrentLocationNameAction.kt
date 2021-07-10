package se.gustavkarlsson.skylight.android.feature.main.state

import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import se.gustavkarlsson.conveyor.Action
import se.gustavkarlsson.conveyor.AtomicStateFlow
import se.gustavkarlsson.skylight.android.lib.reversegeocoder.ReverseGeocoder
import javax.inject.Inject

internal class StreamCurrentLocationNameAction @Inject constructor(
    private val reverseGeocoder: ReverseGeocoder,
) : Action<State> {
    override suspend fun execute(stateFlow: AtomicStateFlow<State>) {
        val locations = stateFlow
            .map { state -> state.currentLocation }
            .distinctUntilChanged()
        reverseGeocoder.stream(locations)
            .collectLatest { result ->
                stateFlow.update {
                    when (this) {
                        is State.Loading -> copy(currentLocationName = result)
                        is State.Ready -> copy(currentLocationName = result)
                    }
                }
            }
    }
}

package se.gustavkarlsson.skylight.android.feature.main.state

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import se.gustavkarlsson.conveyor.Action
import se.gustavkarlsson.conveyor.AtomicStateFlow
import se.gustavkarlsson.skylight.android.core.entities.Loading
import se.gustavkarlsson.skylight.android.lib.reversegeocoder.ReverseGeocoder
import javax.inject.Inject

internal class StreamCurrentLocationNameAction @Inject constructor(
    private val reverseGeocoder: ReverseGeocoder,
) : Action<State> {

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun execute(stateFlow: AtomicStateFlow<State>) {
        stateFlow
            .map { state -> state.currentLocation }
            .distinctUntilChanged()
            .flatMapLatest { loadableLocationResult ->
                loadableLocationResult.fold(
                    ifEmpty = { flowOf(Loading) },
                    ifSome = { locationResult ->
                        locationResult.fold(
                            ifLeft = { flowOf(Loading) },
                            ifRight = { location -> reverseGeocoder.stream(location) }
                        )
                    }
                )
            }
            .distinctUntilChanged()
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

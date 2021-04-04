package se.gustavkarlsson.skylight.android.feature.main.viewmodel

import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.threeten.bp.Duration
import se.gustavkarlsson.conveyor.Store
import se.gustavkarlsson.skylight.android.feature.main.state.State
import se.gustavkarlsson.skylight.android.lib.time.Time
import se.gustavkarlsson.skylight.android.lib.ui.CoroutineScopedService

@ExperimentalCoroutinesApi
internal class MainViewModel @Inject constructor(
    store: Store<State>,
    private val stateToViewStateMapper: StateToViewStateMapper,
    private val eventHandler: EventHandler,
    private val time: Time,
    @MinUpdateInterval private val minUpdateInterval: Duration,
) : CoroutineScopedService() {

    init {
        store.start(scope)
    }

    val state: StateFlow<ViewState> = store.state
        .flatMapLatest { state ->
            flow {
                while (true) {
                    val viewState = stateToViewStateMapper.map(state, time.now())
                    emit(viewState)
                    delay(minUpdateInterval.toMillis())
                }
            }
        }
        .stateIn(scope, SharingStarted.Eagerly, stateToViewStateMapper.map(store.state.value, time.now()))

    fun onEvent(event: Event) {
        scope.launch { eventHandler.onEvent(event) }
    }
}

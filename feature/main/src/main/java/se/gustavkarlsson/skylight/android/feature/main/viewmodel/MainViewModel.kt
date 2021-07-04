package se.gustavkarlsson.skylight.android.feature.main.viewmodel

import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import se.gustavkarlsson.conveyor.Store
import se.gustavkarlsson.skylight.android.feature.main.state.State
import se.gustavkarlsson.skylight.android.lib.ui.CoroutineScopedService
import javax.inject.Inject

internal class MainViewModel @Inject constructor(
    store: Store<State>,
    private val stateToViewStateMapper: StateToViewStateMapper,
    private val eventHandler: EventHandler,
) : CoroutineScopedService() {

    init {
        store.start(scope)
    }

    val state: StateFlow<ViewState> = store.state
        .map { state -> stateToViewStateMapper.map(state) }
        .stateIn(scope, SharingStarted.WhileSubscribed(), stateToViewStateMapper.map(store.state.value))

    fun onEvent(event: Event) {
        scope.launch { eventHandler.onEvent(event) }
    }
}

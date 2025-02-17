package se.gustavkarlsson.skylight.android.feature.main.viewmodel

import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject
import se.gustavkarlsson.conveyor.Store
import se.gustavkarlsson.skylight.android.core.ViewModelScope
import se.gustavkarlsson.skylight.android.core.utils.mapState
import se.gustavkarlsson.skylight.android.feature.main.state.State
import se.gustavkarlsson.skylight.android.lib.ui.CoroutineScopedService

@Inject
@ViewModelScope
internal class MainViewModel(
    store: Store<State>,
    private val stateToViewStateMapper: StateToViewStateMapper,
    private val eventHandler: EventHandler,
) : CoroutineScopedService() {

    init {
        store.start(scope)
    }

    val state: StateFlow<ViewState> = store.state.mapState { state -> stateToViewStateMapper.map(state) }

    fun onEvent(event: Event) {
        scope.launch { eventHandler.onEvent(event) }
    }
}

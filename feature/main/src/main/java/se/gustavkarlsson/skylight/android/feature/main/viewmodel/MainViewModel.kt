package se.gustavkarlsson.skylight.android.feature.main.viewmodel

import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import se.gustavkarlsson.conveyor.Store
import se.gustavkarlsson.conveyor.issue
import se.gustavkarlsson.skylight.android.feature.main.state.Search
import se.gustavkarlsson.skylight.android.feature.main.state.State
import se.gustavkarlsson.skylight.android.lib.navigation.BackPress
import se.gustavkarlsson.skylight.android.lib.ui.CoroutineScopedService

@ExperimentalCoroutinesApi
internal class MainViewModel @Inject constructor(
    private val store: Store<State>,
    private val stateToViewStateMapper: StateToViewStateMapper,
    private val eventHandler: EventHandler,
) : CoroutineScopedService() {

    init {
        store.start(scope)
    }

    val state: StateFlow<ViewState> = store.state
        .map { state -> stateToViewStateMapper.map(state) }
        .stateIn(scope, SharingStarted.Eagerly, stateToViewStateMapper.map(store.state.value))

    fun onEvent(event: Event) {
        scope.launch { eventHandler.onEvent(event) }
    }

    fun onBackPressed(): BackPress {
        return when (store.state.value.search) {
            Search.Closed -> BackPress.NOT_HANDLED
            is Search.Open -> {
                store.issue { state ->
                    state.update { copy(search = Search.Closed) }
                }
                BackPress.HANDLED
            }
        }
    }
}

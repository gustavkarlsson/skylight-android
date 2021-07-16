package se.gustavkarlsson.skylight.android

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.StateFlow
import se.gustavkarlsson.skylight.android.lib.state.State

internal class MainViewModel : ViewModel() {
    val state: StateFlow<State> = TODO()
}

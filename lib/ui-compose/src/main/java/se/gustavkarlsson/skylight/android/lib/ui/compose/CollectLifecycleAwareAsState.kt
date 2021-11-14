package se.gustavkarlsson.skylight.android.lib.ui.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

@Composable
fun <T> StateFlow<T>.collectAsLifecycleAwareState(
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
): State<T> {
    val lifecycleOwner = LocalLifecycleOwner.current
    val lifecycleAwareFlow = remember(this, lifecycleOwner, minActiveState) {
        // FIXME verify when this re-runs
        flowWithLifecycle(lifecycleOwner.lifecycle, minActiveState)
            .stateIn(lifecycleOwner.lifecycleScope, SharingStarted.WhileSubscribed(), initialValue = value)
    }
    return lifecycleAwareFlow.collectAsState()
}

package se.gustavkarlsson.skylight.android.lib.ui.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import kotlinx.coroutines.flow.StateFlow

@Composable
fun <T> StateFlow<T>.collectAsLifecycleAwareState(
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
): State<T> {
    val lifecycleOwner = LocalLifecycleOwner.current
    val lifecycleAwareFlow = remember(key1 = this, key2 = lifecycleOwner, key3 = minActiveState) {
        flowWithLifecycle(lifecycleOwner.lifecycle, minActiveState)
    }
    return lifecycleAwareFlow.collectAsState(initial = value)
}

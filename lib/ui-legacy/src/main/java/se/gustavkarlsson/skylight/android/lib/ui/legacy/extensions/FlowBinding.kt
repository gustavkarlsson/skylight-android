package se.gustavkarlsson.skylight.android.lib.ui.legacy.extensions

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

fun <T : Any> Flow<T>.bind(
    scope: CoroutineScope,
    dispatcher: CoroutineDispatcher = Dispatchers.Main,
    block: suspend (value: T) -> Unit
): Job = scope.launch(dispatcher) {
    this@bind.collect { block(it) }
}

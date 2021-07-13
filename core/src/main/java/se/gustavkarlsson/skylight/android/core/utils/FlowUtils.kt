package se.gustavkarlsson.skylight.android.core.utils

import arrow.core.NonEmptyList
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.AbstractFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.withContext

// TODO Replace these with built-in once available

@OptIn(ExperimentalCoroutinesApi::class)
fun <T> Flow<T>.windowed(size: Int): Flow<NonEmptyList<T>> {
    require(size > 0) { "Requested size $size is non-positive." }
    return scan(emptyList<T>()) { oldItems, newItem ->
        oldItems.takeLast(size - 1) + newItem
    }.filter { items ->
        items.size == size
    }.map { items ->
        items.nonEmptyUnsafe()
    }
}

fun <T> Flow<T>.throttle(waitMillis: Long): Flow<T> = flow {
    coroutineScope {
        val context = coroutineContext
        var nextMillis = 0L
        var delayPost: Deferred<Unit>? = null
        collect {
            val current = System.currentTimeMillis()
            if (nextMillis < current) {
                nextMillis = current + waitMillis
                emit(it)
                delayPost?.cancel()
            } else {
                val delayNext = nextMillis
                delayPost?.cancel()
                delayPost = async(Dispatchers.Default) {
                    delay(nextMillis - current)
                    if (delayNext == nextMillis) {
                        nextMillis = System.currentTimeMillis() + waitMillis
                        withContext(context) {
                            emit(it)
                        }
                    }
                }
            }
        }
    }
}

fun <T, R> StateFlow<T>.mapState(
    transform: (value: T) -> R,
): StateFlow<R> = MappedStateFlow(this, transform)

@OptIn(FlowPreview::class)
private class MappedStateFlow<T, R>(
    private val source: StateFlow<T>,
    private val transform: (T) -> R,
) : AbstractFlow<R>(), StateFlow<R> {

    override val value: R
        get() = transform(source.value)

    override val replayCache: List<R>
        get() = source.replayCache.map(transform)

    override suspend fun collectSafely(collector: FlowCollector<R>) {
        source
            .map { transform(it) }
            .collect { collector.emit(it) }
    }
}

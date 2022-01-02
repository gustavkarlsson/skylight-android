package se.gustavkarlsson.skylight.android.core.utils

import arrow.core.NonEmptyList
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.withContext
import java.util.concurrent.atomic.AtomicReference

// TODO Replace these with built-in once available

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

private class MappedStateFlow<T, R>(
    private val source: StateFlow<T>,
    private val transform: (T) -> R,
) : Flow<R>, StateFlow<R> {

    private val cache = AtomicReference<Pair<T, R>?>(null)

    override val value: R
        get() = cache.getOrRefresh(source.value)

    override val replayCache: List<R>
        get() = listOf(value)

    override suspend fun collect(collector: FlowCollector<R>): Nothing {
        source
            .map { cache.getOrRefresh(it) }
            .collect { collector.emit(it) }
        awaitCancellation()
    }

    private fun AtomicReference<Pair<T, R>?>.getOrRefresh(sourceValue: T): R {
        val result = updateAndGet { cached ->
            if (sourceValue === cached?.first) {
                cached
            } else {
                sourceValue to transform(sourceValue)
            }
        }
        return result!!.second
    }
}

package se.gustavkarlsson.skylight.android.core.utils

import arrow.core.NonEmptyList
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.scan
import java.util.concurrent.atomic.AtomicReference
import kotlin.time.Duration
import kotlin.time.TimeMark
import kotlin.time.TimeSource

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

fun <T> Flow<T>.throttleLatest(minDelay: Duration): Flow<T> = channelFlow {
    val lastEmitTimeRef = AtomicReference<TimeMark?>(null)
    collectLatest { value ->
        val lastEmitTime = lastEmitTimeRef.get()
        if (lastEmitTime != null) {
            val delayLeft = minDelay - lastEmitTime.elapsedNow()
            delay(delayLeft)
        }
        send(value)
        lastEmitTimeRef.set(now())
    }
}

private fun now(): TimeMark = TimeSource.Monotonic.markNow()

fun <T, R> StateFlow<T>.mapState(
    transform: (value: T) -> R,
): StateFlow<R> = MappedStateFlow(this, transform)

private class MappedStateFlow<T, R>(
    private val source: StateFlow<T>,
    private val transform: (T) -> R,
) : Flow<R>, StateFlow<R> {

    private var cache: Pair<T, R>? = null

    override val value: R
        get() = getOrRefresh(source.value)

    override val replayCache: List<R>
        get() = listOf(value)

    override suspend fun collect(collector: FlowCollector<R>): Nothing {
        source
            .map { getOrRefresh(it) }
            .collect { collector.emit(it) }
        awaitCancellation()
    }

    private fun getOrRefresh(sourceValue: T): R {
        val cache = cache
        return if (cache != null && cache.first === sourceValue) {
            return cache.second
        } else {
            val transformed = transform(sourceValue)
            this.cache = sourceValue to transformed
            transformed
        }
    }
}

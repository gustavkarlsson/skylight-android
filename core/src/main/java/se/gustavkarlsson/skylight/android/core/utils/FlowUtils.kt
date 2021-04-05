package se.gustavkarlsson.skylight.android.core.utils

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.scan

// TODO Replace with built-in once available
fun <T> Flow<T>.windowed(size: Int): Flow<List<T>> {
    require(size > 0) { "Requested size $size is non-positive." }
    return scan(emptyList<T>()) { oldItems, newItem ->
        oldItems.takeLast(size - 1) + newItem
    }.filter { it.size == size }
}

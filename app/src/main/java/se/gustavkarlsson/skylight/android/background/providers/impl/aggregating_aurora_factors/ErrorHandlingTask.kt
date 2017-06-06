package se.gustavkarlsson.skylight.android.background.providers.impl.aggregating_aurora_factors

import java.util.concurrent.Callable
import java.util.concurrent.TimeoutException

internal interface ErrorHandlingTask<V> {
    val callable: Callable<V>
    fun handleInterruptedException(e: InterruptedException): V
    fun handleThrowable(e: Throwable): V
    fun handleTimeoutException(e: TimeoutException): V
}

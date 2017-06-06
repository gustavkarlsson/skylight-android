package se.gustavkarlsson.skylight.android.background.providers.impl.aggregating_aurora_factors

import java8.util.function.Function
import org.threeten.bp.Duration
import java.util.concurrent.ExecutionException
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

internal class ErrorHandlingFuture<out V>(
		private val future: Future<V>,
		private val timeout: Duration,
		private val interruptedExceptionHandler: Function<InterruptedException, V>,
		private val throwableHandler: Function<Throwable, V>,
		private val timeoutExceptionHandler: Function<TimeoutException, V>) {

    fun get(): V {
        try {
            return future.get(timeout.toMillis(), TimeUnit.MILLISECONDS)
        } catch (e: InterruptedException) {
            return interruptedExceptionHandler.apply(e)
        } catch (e: ExecutionException) {
            return throwableHandler.apply(e.cause)
        } catch (e: TimeoutException) {
            return timeoutExceptionHandler.apply(e)
        }

    }
}

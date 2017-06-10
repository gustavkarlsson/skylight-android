package se.gustavkarlsson.skylight.android.background.providers.impl.aggregating_aurora_factors

import org.threeten.bp.Duration
import java.util.concurrent.ExecutionException
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

class ErrorHandlingFuture<out V>(
		private val future: Future<V>,
		private val timeout: Duration,
		private val interruptedExceptionHandler: (InterruptedException) -> V,
		private val throwableHandler: (Throwable) -> V,
		private val timeoutExceptionHandler: (TimeoutException) -> V)
{

    fun get(): V {
        try {
            return future.get(timeout.toMillis(), TimeUnit.MILLISECONDS)
        } catch (e: InterruptedException) {
            return interruptedExceptionHandler(e)
        } catch (e: ExecutionException) {
            return throwableHandler(e.cause!!)
        } catch (e: TimeoutException) {
            return timeoutExceptionHandler(e)
        }

    }
}

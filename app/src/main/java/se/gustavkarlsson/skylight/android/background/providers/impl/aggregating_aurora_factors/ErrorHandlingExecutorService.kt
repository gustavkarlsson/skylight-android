package se.gustavkarlsson.skylight.android.background.providers.impl.aggregating_aurora_factors

import dagger.Reusable
import org.threeten.bp.Duration
import se.gustavkarlsson.skylight.android.dagger.Names.CACHED_THREAD_POOL_NAME
import java.util.concurrent.ExecutorService
import javax.inject.Inject
import javax.inject.Named

@Reusable
internal class ErrorHandlingExecutorService
@Inject
constructor(
		@param:Named(CACHED_THREAD_POOL_NAME) private val executorService: ExecutorService
) {

    fun <V> execute(task: ErrorHandlingTask<V>, timeout: Duration): ErrorHandlingFuture<V> {
        val future = executorService.submit(task.callable)
        return ErrorHandlingFuture(future, timeout, task::handleInterruptedException, task::handleThrowable, task::handleTimeoutException)
    }
}

package se.gustavkarlsson.skylight.android.background.providers.impl.aggregating_aurora_factors;

import org.threeten.bp.Duration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import javax.inject.Inject;
import javax.inject.Named;

import dagger.Reusable;

import static se.gustavkarlsson.skylight.android.dagger.Names.CACHED_THREAD_POOL_NAME;

@Reusable
class ErrorHandlingExecutorService {
	private final ExecutorService executorService;

	@Inject
	ErrorHandlingExecutorService(@Named(CACHED_THREAD_POOL_NAME) ExecutorService executorService) {
		this.executorService = executorService;
	}

	<V> ErrorHandlingFuture<V> execute(ErrorHandlingTask<V> task, Duration timeout) {
		Future<V> future = executorService.submit(task.getCallable());
		return new ErrorHandlingFuture<>(future, timeout, task::handleInterruptedException, task::handleThrowable, task::handleTimeoutException);
	}
}

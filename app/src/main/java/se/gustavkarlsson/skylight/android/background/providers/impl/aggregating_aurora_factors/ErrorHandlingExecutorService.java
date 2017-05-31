package se.gustavkarlsson.skylight.android.background.providers.impl.aggregating_aurora_factors;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

class ErrorHandlingExecutorService {
	private final ExecutorService executorService;

	ErrorHandlingExecutorService(ExecutorService executorService) {
		this.executorService = executorService;
	}

	<V> ErrorHandlingFuture<V> execute(ErrorHandlingTask<V> task, long timeoutMillis) {
		Future<V> future = executorService.submit(task.getCallable());
		return new ErrorHandlingFuture<>(future, timeoutMillis, task::handleInterruptedException, task::handleThrowable, task::handleTimeoutException);
	}
}

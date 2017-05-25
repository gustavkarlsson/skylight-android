package se.gustavkarlsson.aurora_notifier.android.background.providers.impl.aggregating_aurora_factors;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import java8.util.function.Function;

class ErrorHandlingFuture<V> {
	private final Future<V> future;
	private final long timeoutMillis;
	private final Function<InterruptedException, V> interruptedExceptionHandler;
	private final Function<Throwable, V> throwableHandler;
	private final Function<TimeoutException, V> timeoutExceptionHandler;

	ErrorHandlingFuture(Future<V> future, long timeoutMillis, Function<InterruptedException, V> interruptedExceptionHandler, Function<Throwable, V> throwableHandler, Function<TimeoutException, V> timeoutExceptionHandler) {
		this.future = future;
		this.timeoutMillis = timeoutMillis;
		this.interruptedExceptionHandler = interruptedExceptionHandler;
		this.throwableHandler = throwableHandler;
		this.timeoutExceptionHandler = timeoutExceptionHandler;
	}

	V get() {
		try {
			return future.get(timeoutMillis, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			return interruptedExceptionHandler.apply(e);
		} catch (ExecutionException e) {
			return throwableHandler.apply(e.getCause());
		} catch (TimeoutException e) {
			return timeoutExceptionHandler.apply(e);
		}
	}
}

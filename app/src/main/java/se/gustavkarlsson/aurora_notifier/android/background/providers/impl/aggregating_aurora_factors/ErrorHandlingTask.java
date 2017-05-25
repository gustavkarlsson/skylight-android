package se.gustavkarlsson.aurora_notifier.android.background.providers.impl.aggregating_aurora_factors;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeoutException;

interface ErrorHandlingTask<V> {
	Callable<V> getCallable();
	V handleInterruptedException(InterruptedException e);
	V handleThrowable(Throwable e);
	V handleTimeoutException(TimeoutException e);
}

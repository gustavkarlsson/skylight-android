package se.gustavkarlsson.aurora_notifier.android.background.providers.impl.aggregating_aurora_factors;

import java.util.concurrent.Callable;

interface DefaultingCallable<V> extends Callable<V> {
	V getDefault();
}

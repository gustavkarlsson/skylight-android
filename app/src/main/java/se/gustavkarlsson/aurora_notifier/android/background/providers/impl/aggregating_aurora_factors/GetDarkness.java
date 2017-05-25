package se.gustavkarlsson.aurora_notifier.android.background.providers.impl.aggregating_aurora_factors;

import android.location.Location;
import android.util.Log;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeoutException;

import se.gustavkarlsson.aurora_notifier.android.background.providers.DarknessProvider;
import se.gustavkarlsson.aurora_notifier.android.models.factors.Darkness;

class GetDarkness implements ErrorHandlingTask<Darkness> {
	private static final String TAG = GetDarkness.class.getSimpleName();

	private final DarknessProvider provider;
	private final Location location;
	private final long timeMillis;

	GetDarkness(DarknessProvider provider, Location location, long timeMillis) {
		this.provider = provider;
		this.location = location;
		this.timeMillis = timeMillis;
	}

	@Override
	public Callable<Darkness> getCallable() {
		return this::call;
	}

	private Darkness call() {
		Log.i(TAG, "Getting darkness...");
		Darkness darkness = provider.getDarkness(timeMillis, location.getLatitude(), location.getLongitude());
		Log.d(TAG, "Darkness is: " + darkness);
		return darkness;
	}

	@Override
	public Darkness handleInterruptedException(InterruptedException e) {
		return new Darkness();
	}

	@Override
	public Darkness handleThrowable(Throwable e) {
		return new Darkness();
	}

	@Override
	public Darkness handleTimeoutException(TimeoutException e) {
		return new Darkness();
	}
}

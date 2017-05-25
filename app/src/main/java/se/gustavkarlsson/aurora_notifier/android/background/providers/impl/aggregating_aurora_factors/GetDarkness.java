package se.gustavkarlsson.aurora_notifier.android.background.providers.impl.aggregating_aurora_factors;

import android.location.Location;
import android.util.Log;

import se.gustavkarlsson.aurora_notifier.android.background.providers.DarknessProvider;
import se.gustavkarlsson.aurora_notifier.android.models.factors.Darkness;

class GetDarkness implements DefaultingCallable<Darkness> {
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
	public Darkness call() {
		Log.i(TAG, "Getting darkness...");
		Darkness darkness = provider.getDarkness(timeMillis, location.getLatitude(), location.getLongitude());
		Log.d(TAG, "Darkness is: " + darkness);
		return darkness;
	}

	@Override
	public Darkness getDefault() {
		return new Darkness();
	}
}

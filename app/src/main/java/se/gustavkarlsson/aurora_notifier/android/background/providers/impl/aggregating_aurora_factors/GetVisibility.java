package se.gustavkarlsson.aurora_notifier.android.background.providers.impl.aggregating_aurora_factors;

import android.location.Location;
import android.util.Log;

import se.gustavkarlsson.aurora_notifier.android.background.providers.VisibilityProvider;
import se.gustavkarlsson.aurora_notifier.android.models.factors.Visibility;

class GetVisibility implements DefaultingCallable<Visibility> {
	private static final String TAG = GetVisibility.class.getSimpleName();

	private final VisibilityProvider provider;
	private final Location location;

	GetVisibility(VisibilityProvider provider, Location location) {
		this.provider = provider;
		this.location = location;
	}

	@Override
	public Visibility call() throws Exception {
		Log.i(TAG, "Getting visibility...");
		Visibility visibility = provider.getVisibility(location.getLatitude(), location.getLongitude());
		Log.d(TAG, "Visibility is:  " + visibility);
		return visibility;
	}

	@Override
	public Visibility getDefault() {
		return new Visibility();
	}
}

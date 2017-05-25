package se.gustavkarlsson.aurora_notifier.android.background.providers.impl.aggregating_aurora_factors;

import android.location.Location;
import android.util.Log;

import se.gustavkarlsson.aurora_notifier.android.background.providers.GeomagLocationProvider;
import se.gustavkarlsson.aurora_notifier.android.models.factors.GeomagLocation;

class GetGeomagLocation implements DefaultingCallable<GeomagLocation> {
	private static final String TAG = GetGeomagLocation.class.getSimpleName();

	private final GeomagLocationProvider provider;
	private final Location location;

	GetGeomagLocation(GeomagLocationProvider provider, Location location) {
		this.provider = provider;
		this.location = location;
	}

	@Override
	public GeomagLocation call() throws Exception {
		Log.i(TAG, "Getting geomagnetic location...");
		GeomagLocation geomagLocation = provider.getGeomagLocation(location.getLatitude(), location.getLongitude());
		Log.d(TAG, "Geomagnetic location is: " + geomagLocation);
		return geomagLocation;
	}

	@Override
	public GeomagLocation getDefault() {
		return new GeomagLocation();
	}
}

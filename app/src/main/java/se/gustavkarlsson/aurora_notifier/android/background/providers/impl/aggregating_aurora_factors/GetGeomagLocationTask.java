package se.gustavkarlsson.aurora_notifier.android.background.providers.impl.aggregating_aurora_factors;

import android.location.Location;
import android.util.Log;

import java.util.concurrent.FutureTask;

import se.gustavkarlsson.aurora_notifier.android.background.providers.GeomagLocationProvider;
import se.gustavkarlsson.aurora_notifier.android.models.factors.GeomagLocation;

class GetGeomagLocationTask extends FutureTask<GeomagLocation> {
	private static final String TAG = GetGeomagLocationTask.class.getSimpleName();

	GetGeomagLocationTask(GeomagLocationProvider provider, Location location) {
		super(() -> call(provider, location));
	}

	private static GeomagLocation call(GeomagLocationProvider provider, Location location) throws Exception {
		Log.i(TAG, "Getting geomagnetic location...");
		GeomagLocation geomagLocation = provider.getGeomagLocation(location.getLatitude(), location.getLongitude());
		Log.d(TAG, "Geomagnetic location is: " + geomagLocation);
		return geomagLocation;
	}
}

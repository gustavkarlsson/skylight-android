package se.gustavkarlsson.aurora_notifier.android.background.tasks;

import android.location.Location;
import android.util.Log;

import java.util.concurrent.FutureTask;

import se.gustavkarlsson.aurora_notifier.android.background.providers.GeomagneticLocationProvider;
import se.gustavkarlsson.aurora_notifier.android.models.data.GeomagneticLocation;

public class GetGeomagneticLocationTask extends FutureTask<GeomagneticLocation> {
	private static final String TAG = GetGeomagneticLocationTask.class.getSimpleName();

	public GetGeomagneticLocationTask(GeomagneticLocationProvider provider, Location location) {
		super(() -> call(provider, location));
	}

	private static GeomagneticLocation call(GeomagneticLocationProvider provider, Location location) throws Exception {
		Log.i(TAG, "Getting geomagnetic location...");
		GeomagneticLocation geomagneticLocation = provider.getGeomagneticLocation(location.getLatitude(), location.getLongitude());
		Log.d(TAG, "Geomagnetic location is: " + geomagneticLocation);
		return geomagneticLocation;
	}
}

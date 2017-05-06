package se.gustavkarlsson.aurora_notifier.android.background.tasks;

import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import se.gustavkarlsson.aurora_notifier.android.background.providers.GeomagneticLocationProvider;
import se.gustavkarlsson.aurora_notifier.android.models.data.GeomagneticLocation;

public class GetGeomagneticLocationTask extends AsyncTask<Object, Void, GeomagneticLocation> {
	private static final String TAG = GetGeomagneticLocationTask.class.getSimpleName();

	private final GeomagneticLocationProvider provider;
	private final Location location;

	public GetGeomagneticLocationTask(GeomagneticLocationProvider provider, Location location) {
		this.provider = provider;
		this.location = location;
	}

	@Override
	protected GeomagneticLocation doInBackground(Object... params) {
		Log.i(TAG, "Getting degrees from closest pole...");
		GeomagneticLocation geomagneticLocation = provider.getGeomagneticLocation(location.getLatitude(), location.getLongitude());
		Log.d(TAG, "Geomagnetic location is: " + geomagneticLocation);
		return geomagneticLocation;
	}
}

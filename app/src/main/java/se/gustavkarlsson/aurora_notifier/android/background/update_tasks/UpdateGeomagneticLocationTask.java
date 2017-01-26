package se.gustavkarlsson.aurora_notifier.android.background.update_tasks;

import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import se.gustavkarlsson.aurora_notifier.android.R;
import se.gustavkarlsson.aurora_notifier.android.background.ValueOrError;
import se.gustavkarlsson.aurora_notifier.android.background.providers.GeomagneticLocationProvider;
import se.gustavkarlsson.aurora_notifier.android.models.factors.GeomagneticLocation;

import static se.gustavkarlsson.aurora_notifier.android.background.ValueOrError.error;
import static se.gustavkarlsson.aurora_notifier.android.background.ValueOrError.value;

public class UpdateGeomagneticLocationTask extends AsyncTask<Object, Void, ValueOrError<GeomagneticLocation>> {
	private static final String TAG = UpdateGeomagneticLocationTask.class.getSimpleName();

	private final GeomagneticLocationProvider provider;
	private final Location location;

	public UpdateGeomagneticLocationTask(GeomagneticLocationProvider provider, Location location) {
		this.provider = provider;
		this.location = location;
	}

	@Override
	protected ValueOrError<GeomagneticLocation> doInBackground(Object... params) {
		Log.i(TAG, "Getting degrees from closest pole...");
		GeomagneticLocation geomagneticLocation = provider.getGeomagneticLocation(location.getLatitude(), location.getLongitude());
		Log.d(TAG, "Geomagnetic location is: " + geomagneticLocation);
		if (geomagneticLocation == null) {
			return error(R.string.could_not_determine_location);
		}
		return value(geomagneticLocation);
	}
}

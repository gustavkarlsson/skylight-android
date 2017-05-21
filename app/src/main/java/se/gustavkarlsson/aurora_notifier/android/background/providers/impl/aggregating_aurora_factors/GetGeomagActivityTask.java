package se.gustavkarlsson.aurora_notifier.android.background.providers.impl.aggregating_aurora_factors;

import android.util.Log;

import java.util.concurrent.FutureTask;

import se.gustavkarlsson.aurora_notifier.android.background.providers.GeomagActivityProvider;
import se.gustavkarlsson.aurora_notifier.android.models.factors.GeomagActivity;

class GetGeomagActivityTask extends FutureTask<GeomagActivity> {
	private static final String TAG = GetGeomagActivityTask.class.getSimpleName();

	GetGeomagActivityTask(GeomagActivityProvider provider) {
		super(() -> call(provider));
	}

	private static GeomagActivity call(GeomagActivityProvider provider) {
		Log.i(TAG, "Getting geomagnetic activity...");
		GeomagActivity geomagActivity = provider.getGeomagActivity();
		Log.d(TAG, "Geomagnetic activity is: " + geomagActivity);
		return geomagActivity;
	}
}

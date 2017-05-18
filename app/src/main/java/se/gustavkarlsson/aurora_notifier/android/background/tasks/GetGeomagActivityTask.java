package se.gustavkarlsson.aurora_notifier.android.background.tasks;

import android.util.Log;

import java.util.concurrent.FutureTask;

import se.gustavkarlsson.aurora_notifier.android.background.providers.GeomagActivityProvider;
import se.gustavkarlsson.aurora_notifier.android.models.factors.GeomagActivity;

public class GetGeomagActivityTask extends FutureTask<GeomagActivity> {
	private static final String TAG = GetGeomagActivityTask.class.getSimpleName();

	public GetGeomagActivityTask(GeomagActivityProvider provider) {
		super(() -> call(provider));
	}

	private static GeomagActivity call(GeomagActivityProvider provider) {
		Log.i(TAG, "Getting geomag activity...");
		GeomagActivity geomagActivity = provider.getGeomagActivity();
		Log.d(TAG, "Geomag activity is: " + geomagActivity);
		return geomagActivity;
	}
}

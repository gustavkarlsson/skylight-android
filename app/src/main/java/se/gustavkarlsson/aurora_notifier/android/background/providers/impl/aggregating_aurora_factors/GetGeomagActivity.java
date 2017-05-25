package se.gustavkarlsson.aurora_notifier.android.background.providers.impl.aggregating_aurora_factors;

import android.util.Log;

import se.gustavkarlsson.aurora_notifier.android.background.providers.GeomagActivityProvider;
import se.gustavkarlsson.aurora_notifier.android.models.factors.GeomagActivity;

class GetGeomagActivity implements DefaultingCallable<GeomagActivity> {
	private static final String TAG = GetGeomagActivity.class.getSimpleName();

	private final GeomagActivityProvider provider;

	GetGeomagActivity(GeomagActivityProvider provider) {
		this.provider = provider;
	}

	@Override
	public GeomagActivity call() throws Exception {
		Log.i(TAG, "Getting geomagnetic activity...");
		GeomagActivity geomagActivity = provider.getGeomagActivity();
		Log.d(TAG, "Geomagnetic activity is: " + geomagActivity);
		return geomagActivity;
	}

	@Override
	public GeomagActivity getDefault() {
		return new GeomagActivity();
	}
}

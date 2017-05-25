package se.gustavkarlsson.aurora_notifier.android.background.providers.impl.aggregating_aurora_factors;

import android.util.Log;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeoutException;

import se.gustavkarlsson.aurora_notifier.android.background.providers.GeomagActivityProvider;
import se.gustavkarlsson.aurora_notifier.android.models.factors.GeomagActivity;

class GetGeomagActivity implements ErrorHandlingTask<GeomagActivity> {
	private static final String TAG = GetGeomagActivity.class.getSimpleName();

	private final GeomagActivityProvider provider;

	GetGeomagActivity(GeomagActivityProvider provider) {
		this.provider = provider;
	}

	@Override
	public Callable<GeomagActivity> getCallable() {
		return this::call;
	}

	private GeomagActivity call() throws Exception {
		Log.i(TAG, "Getting geomagnetic activity...");
		GeomagActivity geomagActivity = provider.getGeomagActivity();
		Log.d(TAG, "Geomagnetic activity is: " + geomagActivity);
		return geomagActivity;
	}

	@Override
	public GeomagActivity handleInterruptedException(InterruptedException e) {
		return new GeomagActivity();
	}

	@Override
	public GeomagActivity handleThrowable(Throwable e) {
		return new GeomagActivity();
	}

	@Override
	public GeomagActivity handleTimeoutException(TimeoutException e) {
		return new GeomagActivity();
	}
}

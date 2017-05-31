package se.gustavkarlsson.skylight.android.background.providers.impl.aggregating_aurora_factors;

import android.location.Location;
import android.util.Log;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeoutException;

import se.gustavkarlsson.skylight.android.background.providers.GeomagLocationProvider;
import se.gustavkarlsson.skylight.android.models.factors.GeomagLocation;

class GetGeomagLocation implements ErrorHandlingTask<GeomagLocation> {
	private static final String TAG = GetGeomagLocation.class.getSimpleName();

	private final GeomagLocationProvider provider;
	private final Location location;

	GetGeomagLocation(GeomagLocationProvider provider, Location location) {
		this.provider = provider;
		this.location = location;
	}

	@Override
	public Callable<GeomagLocation> getCallable() {
		return this::call;
	}

	private GeomagLocation call() throws Exception {
		Log.i(TAG, "Getting geomagnetic location...");
		GeomagLocation geomagLocation = provider.getGeomagLocation(location.getLatitude(), location.getLongitude());
		Log.d(TAG, "Geomagnetic location is: " + geomagLocation);
		return geomagLocation;
	}

	@Override
	public GeomagLocation handleInterruptedException(InterruptedException e) {
		return new GeomagLocation();
	}

	@Override
	public GeomagLocation handleThrowable(Throwable e) {
		return new GeomagLocation();
	}

	@Override
	public GeomagLocation handleTimeoutException(TimeoutException e) {
		return new GeomagLocation();
	}
}

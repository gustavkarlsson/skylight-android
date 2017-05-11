package se.gustavkarlsson.aurora_notifier.android.background.providers.impl;

import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import se.gustavkarlsson.aurora_notifier.android.background.providers.AsyncAddressProvider;

public class GeocoderAsyncAddressProvider implements AsyncAddressProvider {
	private static final String TAG = GeocoderAsyncAddressProvider.class.getSimpleName();

	private static final Executor EXECUTOR = AsyncTask.THREAD_POOL_EXECUTOR;
	private final Geocoder geocoder;

	public GeocoderAsyncAddressProvider(Geocoder geocoder) {
		this.geocoder = geocoder;
	}

	@Override
	public Future<Address> execute(double latitude, double longitude) {
		GetAddressTask getAddressTask = new GetAddressTask(latitude, longitude);
		EXECUTOR.execute(getAddressTask);
		return getAddressTask;
	}

	private class GetAddressTask extends FutureTask<Address> {

		GetAddressTask(double latitude, double longitude) {
			super(() -> {
				try {
					List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
					if (addresses.isEmpty()) {
						return null;
					}
					return addresses.get(0);
				} catch (IOException e) {
					Log.e(TAG, String.format("Failed to perform reverse geocoding latitude: %f, longitude: %f", latitude, longitude), e);
					return null;
				}
			});
		}
	}
}

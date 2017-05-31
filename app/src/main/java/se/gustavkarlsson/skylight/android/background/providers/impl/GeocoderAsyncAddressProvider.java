package se.gustavkarlsson.skylight.android.background.providers.impl;

import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import javax.inject.Inject;
import javax.inject.Named;

import se.gustavkarlsson.skylight.android.background.providers.AsyncAddressProvider;

import static se.gustavkarlsson.skylight.android.dagger.modules.ExecutorModule.CACHED_THREAD_POOL;

public class GeocoderAsyncAddressProvider implements AsyncAddressProvider {
	private static final String TAG = GeocoderAsyncAddressProvider.class.getSimpleName();

	private final ExecutorService cachedThreadPool;
	private final Geocoder geocoder;

	@Inject
	GeocoderAsyncAddressProvider(Geocoder geocoder, @Named(CACHED_THREAD_POOL) ExecutorService cachedThreadPool) {
		this.geocoder = geocoder;
		this.cachedThreadPool = cachedThreadPool;
	}

	@Override
	public Future<Address> execute(double latitude, double longitude) {
		GetAddressTask getAddressTask = new GetAddressTask(latitude, longitude);
		cachedThreadPool.execute(getAddressTask);
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
					Log.w(TAG, "Failed to perform reverse geocoding", e);
					return null;
				}
			});
		}
	}
}

package se.gustavkarlsson.aurora_notifier.android.background.providers.impl;

import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeoutException;

import se.gustavkarlsson.aurora_notifier.android.background.providers.AddressProvider;

import static com.google.android.gms.internal.zzt.TAG;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class GeocoderAddressProvider implements AddressProvider {
	private final Executor executor = Executors.newCachedThreadPool();
	private final Geocoder geocoder;

	public GeocoderAddressProvider(Geocoder geocoder) {
		this.geocoder = geocoder;
	}

	@Override
	public Address getAddress(double latitude, double longitude, long timeoutMillis) {
		GetAddressTask getAddressTask = new GetAddressTask(latitude, longitude);
		executor.execute(getAddressTask);
		try {
			return getAddressTask.get(timeoutMillis, MILLISECONDS);
		} catch (TimeoutException e) {
			Log.e(TAG, "Getting address timed out after " + timeoutMillis + "ms", e);
		} catch (ExecutionException e) {
			Throwable cause = e.getCause();
			Log.e(TAG, "An unexpected exception occurred", cause);
		} catch (InterruptedException | CancellationException e) {
			Log.e(TAG, "An unexpected exception occurred", e);
		}
		return null;
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
					Log.e(TAG, String.format(Locale.ENGLISH, "Failed to perform reverse geocoding latitude: %f, longitude: %f", latitude, longitude), e);
					return null;
				}
			});
		}
	}
}

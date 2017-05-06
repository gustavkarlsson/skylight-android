package se.gustavkarlsson.aurora_notifier.android.background.providers.impl;

import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;

import se.gustavkarlsson.aurora_notifier.android.R;
import se.gustavkarlsson.aurora_notifier.android.background.providers.AddressProvider;
import se.gustavkarlsson.aurora_notifier.android.util.UserFriendlyException;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class GeocoderAddressProvider implements AddressProvider {
	private final Executor executor = Executors.newCachedThreadPool();
	private final Geocoder geocoder;

	public GeocoderAddressProvider(Geocoder geocoder) {
		this.geocoder = geocoder;
	}

	@Override
	public Address getAddress(double latitude, double longitude, long timeoutMillis) {
		AsyncTask<Double, Void, Address> getAddressTask = new GetAddressTask().executeOnExecutor(executor, latitude, longitude);
		try {
			return getAddressTask.get(timeoutMillis, MILLISECONDS);
		} catch (InterruptedException | ExecutionException e) {
			throw new UserFriendlyException(R.string.error_unknown_update_error, e);
		} catch (TimeoutException e) {
			throw new UserFriendlyException(R.string.error_updating_took_too_long, e);
		}
	}

	private class GetAddressTask extends AsyncTask<Double, Void, Address> {
		private final String TAG = GetAddressTask.class.getSimpleName();

		@Override
		protected Address doInBackground(Double... params) {
			double latitude = params[0];
			double longitude = params[1];
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
		}
	}
}

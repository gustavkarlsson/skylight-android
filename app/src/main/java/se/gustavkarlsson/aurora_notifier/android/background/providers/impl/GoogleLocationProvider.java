package se.gustavkarlsson.aurora_notifier.android.background.providers.impl;

import android.location.Location;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import se.gustavkarlsson.aurora_notifier.android.R;
import se.gustavkarlsson.aurora_notifier.android.background.ValueOrError;
import se.gustavkarlsson.aurora_notifier.android.background.providers.LocationProvider;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static se.gustavkarlsson.aurora_notifier.android.background.ValueOrError.error;
import static se.gustavkarlsson.aurora_notifier.android.background.ValueOrError.value;


public class GoogleLocationProvider implements LocationProvider {
	private static final String TAG = GoogleLocationProvider.class.getSimpleName();

	private final GoogleApiClient googleApiClient;

	public GoogleLocationProvider(GoogleApiClient googleApiClient) {
		this.googleApiClient = googleApiClient;
	}

	@Override
	public ValueOrError<Location> getLocation(long timeoutMillis) {
		Log.i(TAG, "Connecting to Google Play Services...");
		try {
			ConnectionResult connectionResult = googleApiClient.blockingConnect(timeoutMillis, MILLISECONDS);
			if (!connectionResult.isSuccess()) {
				Log.w(TAG, "Could not connect to Google Play Services" +
						". Error code: " + connectionResult.getErrorCode() +
						". Error message: " + connectionResult.getErrorMessage());
				if (connectionResult.getErrorCode() == ConnectionResult.TIMEOUT) {
					return error(R.string.update_took_too_long);
				}
				return error(R.string.could_not_connect_to_google_play_services);
			}
			Log.d(TAG, "Successfully connected to Google Play Services");
			Log.d(TAG, "Getting location");
			Location location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
			Log.d(TAG, "Location is: " + location);
			if (location == null) {
				error(R.string.could_not_determine_location);
			}
			return value(location);
		} catch (SecurityException e) {
			Log.w(TAG, "Location permission missing", e);
			return error(R.string.location_permission_missing);
		} finally {
			if (googleApiClient.isConnected()) {
				Log.d(TAG, "Disconnecting from Google Play Services");
				googleApiClient.disconnect();
			}
		}
	}
}

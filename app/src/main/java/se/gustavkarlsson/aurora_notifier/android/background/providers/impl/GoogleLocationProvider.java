package se.gustavkarlsson.aurora_notifier.android.background.providers.impl;

import android.location.Location;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import se.gustavkarlsson.aurora_notifier.android.R;
import se.gustavkarlsson.aurora_notifier.android.background.providers.LocationProvider;
import se.gustavkarlsson.aurora_notifier.android.util.UserFriendlyException;

import static java.util.concurrent.TimeUnit.MILLISECONDS;


public class GoogleLocationProvider implements LocationProvider {
	private static final String TAG = GoogleLocationProvider.class.getSimpleName();

	private final GoogleApiClient googleApiClient;

	public GoogleLocationProvider(GoogleApiClient googleApiClient) {
		this.googleApiClient = googleApiClient;
	}

	@Override
	public Location getLocation(long timeoutMillis) {
		Log.i(TAG, "Connecting to Google Play Services...");
		try {
			ConnectionResult connectionResult = googleApiClient.blockingConnect(timeoutMillis, MILLISECONDS);
			if (!connectionResult.isSuccess()) {
				if (connectionResult.getErrorCode() == ConnectionResult.TIMEOUT) {
					throw new UserFriendlyException(R.string.update_took_too_long, "Connecting to Google API timed out after " + timeoutMillis + "ms");
				}
				throw new UserFriendlyException(R.string.could_not_connect_to_google_play_services, createErrorMessage(connectionResult));
			}
			Log.d(TAG, "Successfully connected to Google Play Services");
			Log.d(TAG, "Getting location...");
			Location location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
			Log.d(TAG, "Location is: " + location);
			if (location == null) {
				throw new UserFriendlyException(R.string.could_not_determine_location, "Location API returned null");
			}
			return location;
		} catch (SecurityException e) {
			Log.w(TAG, "Location permission missing", e);
			throw new UserFriendlyException(R.string.location_permission_missing, e);
		} finally {
			if (googleApiClient.isConnected()) {
				Log.d(TAG, "Disconnecting from Google Play Services");
				googleApiClient.disconnect();
			}
		}
	}

	@NonNull
	private static String createErrorMessage(ConnectionResult connectionResult) {
		return "Could not connect to Google Play Services" +
                ". Error code: " + connectionResult.getErrorCode() +
                ". Error message: " + connectionResult.getErrorMessage();
	}
}

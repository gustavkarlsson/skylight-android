package se.gustavkarlsson.skylight.android.background.providers.impl

import android.location.Location
import android.util.Log
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationServices
import dagger.Reusable
import org.threeten.bp.Duration
import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.background.providers.LocationProvider
import se.gustavkarlsson.skylight.android.util.UserFriendlyException
import java.util.concurrent.TimeUnit.MILLISECONDS
import javax.inject.Inject

@Reusable
internal class GoogleLocationProvider
@Inject
constructor(
		private val googleApiClient: GoogleApiClient
) : LocationProvider {

    override fun getLocation(timeout: Duration): Location {
        Log.i(TAG, "Connecting to Google Play Services...")
        try {
            val connectionResult = googleApiClient.blockingConnect(timeout.toMillis(), MILLISECONDS)
			handleFailure(connectionResult, timeout)
            Log.d(TAG, "Successfully connected to Google Play Services")
            Log.d(TAG, "Getting location...")
            val location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient)
            Log.d(TAG, "Location is: " + location)
            if (location == null) {
                throw UserFriendlyException(R.string.error_could_not_determine_location, "Location API returned null")
            }
            return location
        } catch (e: SecurityException) {
            Log.w(TAG, "Location permission missing", e)
            throw UserFriendlyException(R.string.error_location_permission_missing, e)
        } finally {
            if (googleApiClient.isConnected) {
                Log.d(TAG, "Disconnecting from Google Play Services")
                googleApiClient.disconnect()
            }
        }
    }

	private fun handleFailure(connectionResult: ConnectionResult, timeout: Duration) {
		if (!connectionResult.isSuccess) {
			if (connectionResult.errorCode == ConnectionResult.TIMEOUT) {
				throw UserFriendlyException(R.string.error_updating_took_too_long, "Connecting to Google API timed out after " + timeout.toMillis() + "ms")
			}
			throw UserFriendlyException(R.string.error_could_not_connect_to_google_play_services, createErrorMessage(connectionResult))
		}
	}

	private fun createErrorMessage(connectionResult: ConnectionResult): String {
		return "Could not connect to Google Play Services" +
				". Error code: " + connectionResult.errorCode +
				". Error message: " + connectionResult.errorMessage
	}

    companion object {
        private val TAG = GoogleLocationProvider::class.java.simpleName
    }
}

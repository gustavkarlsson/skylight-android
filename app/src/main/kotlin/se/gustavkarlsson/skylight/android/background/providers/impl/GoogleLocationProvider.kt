package se.gustavkarlsson.skylight.android.background.providers.impl

import android.location.Location
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationServices
import dagger.Reusable
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.debug
import org.jetbrains.anko.info
import org.jetbrains.anko.warn
import org.threeten.bp.Duration
import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.background.providers.LocationProvider
import se.gustavkarlsson.skylight.android.util.UserFriendlyException
import java.util.concurrent.TimeUnit.MILLISECONDS
import javax.inject.Inject

@Reusable
class GoogleLocationProvider
@Inject
constructor(
		private val googleApiClient: GoogleApiClient
) : LocationProvider, AnkoLogger {

    override fun getLocation(timeout: Duration): Location {
        info("Connecting to Google Play Services...")
        try {
            val connectionResult = googleApiClient.blockingConnect(timeout.toMillis(), MILLISECONDS)
			handleFailure(connectionResult, timeout)
            debug("Successfully connected to Google Play Services")
            debug("Getting location...")
            val location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient)
            debug("Location is: $location")
            if (location == null) {
                throw UserFriendlyException(R.string.error_could_not_determine_location, "Location API returned null")
            }
            return location
        } catch (e: SecurityException) {
            warn("Location permission missing", e)
            throw UserFriendlyException(R.string.error_location_permission_missing, e)
        } finally {
            if (googleApiClient.isConnected) {
                debug("Disconnecting from Google Play Services")
                googleApiClient.disconnect()
            }
        }
    }

	private fun handleFailure(connectionResult: ConnectionResult, timeout: Duration) {
		if (!connectionResult.isSuccess) {
			if (connectionResult.errorCode == ConnectionResult.TIMEOUT) {
				throw UserFriendlyException(R.string.error_updating_took_too_long, "Connecting to Google API timed out after ${timeout.toMillis()}ms")
			}
			throw UserFriendlyException(R.string.error_could_not_connect_to_google_play_services, createErrorMessage(connectionResult))
		}
	}

	private fun createErrorMessage(connectionResult: ConnectionResult): String {
		return "Could not connect to Google Play Services. Error code: ${connectionResult.errorCode}. Error message: ${connectionResult.errorMessage}"
	}
}

package se.gustavkarlsson.skylight.android.services_impl.providers

import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.tasks.Task
import dagger.Reusable
import kotlinx.coroutines.experimental.delay
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.debug
import org.jetbrains.anko.warn
import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.services.Location
import se.gustavkarlsson.skylight.android.services.providers.LocationProvider
import se.gustavkarlsson.skylight.android.util.UserFriendlyException
import javax.inject.Inject

@Reusable
class AndroidLocationProvider
@Inject
constructor(
	private val fusedLocationProviderClient: FusedLocationProviderClient
) : LocationProvider, AnkoLogger {

    suspend override fun getLocation(): Location {
        try {
            debug("Getting location...")
			val getLocationTask = fusedLocationProviderClient.lastLocation
			waitForLocation(getLocationTask)
			checkForErrorsError(getLocationTask)
			val location = getLocationTask.result
            debug("Location is: $location")
            return Location(location.latitude, location.longitude)
        } catch (e: SecurityException) {
            warn("Location permission missing", e)
            throw UserFriendlyException(R.string.error_location_permission_missing, e)
        }
    }

	private suspend fun waitForLocation(getLocationTask: Task<android.location.Location>) {
		var timeRemaining = timeoutMillis
		while (!getLocationTask.isComplete && timeRemaining > 0) {
			delay(pauseMillis)
			timeRemaining -= pauseMillis
		}
	}

	private fun checkForErrorsError(getLocationTask: Task<android.location.Location>) {
		if (!getLocationTask.isComplete) {
			throw UserFriendlyException(R.string.error_could_not_determine_location, "Timed out after $timeoutMillis ms")
		}
		getLocationTask.exception?.let {
			throw UserFriendlyException(R.string.error_could_not_determine_location, it)
		}
		if (getLocationTask.result == null) {
			throw UserFriendlyException(R.string.error_could_not_determine_location, "Location API returned null")
		}
	}

	companion object {
	    private val pauseMillis = 50L
		private val timeoutMillis = 5000L
	}
}

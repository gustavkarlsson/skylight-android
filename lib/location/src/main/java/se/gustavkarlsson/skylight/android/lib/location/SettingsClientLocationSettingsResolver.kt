package se.gustavkarlsson.skylight.android.lib.location

import android.app.Activity
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.location.LocationSettingsStatusCodes
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.suspendCancellableCoroutine
import se.gustavkarlsson.skylight.android.core.logging.logError
import se.gustavkarlsson.skylight.android.core.logging.logInfo
import se.gustavkarlsson.skylight.android.core.logging.logWarn
import kotlin.coroutines.resume

internal class SettingsClientLocationSettingsResolver(locationRequest: LocationRequest) : LocationSettingsResolver {

    private val settingsRequest = with(LocationSettingsRequest.Builder()) {
        addLocationRequest(locationRequest)
        build()
    }

    override suspend fun resolve(activity: Activity): Resolution {
        logInfo { "Resolving location settings" }
        return suspendCancellableCoroutine { continuation ->
            LocationServices.getSettingsClient(activity).checkLocationSettings(settingsRequest)
                .addOnCompleteListener { task ->
                    val resolution = doResolve(task, activity)
                    continuation.resume(resolution)
                }.addOnCanceledListener {
                    logInfo { "Cancelled" }
                    continuation.cancel()
                }
        }
    }

    private fun doResolve(
        task: Task<LocationSettingsResponse>,
        activity: Activity,
    ) = try {
        task.getResult(ApiException::class.java)
        logInfo { "Resolution not needed" }
        Resolution.NotNeeded
    } catch (exception: ApiException) {
        when (exception.statusCode) {
            LocationSettingsStatusCodes.RESOLUTION_REQUIRED ->
                try {
                    val resolvable = exception as ResolvableApiException
                    logInfo { "Offering resolution" }
                    resolvable.startResolutionForResult(activity, 54360263)
                    Resolution.Offered
                } catch (e: Exception) {
                    logError(e) { "Unexpected exception" }
                    Resolution.Unavailable
                }
            LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                logWarn { "No resolution available" }
                Resolution.Unavailable
            }
            else -> {
                logError {
                    "Unexpected status code: ${exception.statusCode} " +
                        "with message: ${exception.status.statusMessage}"
                }
                Resolution.Unavailable
            }
        }
    } catch (e: Exception) {
        logError(e) { "Unexpected exception" }
        Resolution.Unavailable
    }
}

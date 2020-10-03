package se.gustavkarlsson.skylight.android.lib.location

import android.os.Looper
import com.dropbox.android.external.store4.Fetcher
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import org.threeten.bp.Duration
import se.gustavkarlsson.skylight.android.core.logging.logDebug
import se.gustavkarlsson.skylight.android.core.logging.logError
import se.gustavkarlsson.skylight.android.core.logging.logWarn
import android.location.Location as AndroidLocation
import com.google.android.gms.location.LocationResult as GmsLocationResult

@ExperimentalCoroutinesApi
internal fun createLocationFetcher(
    client: FusedLocationProviderClient,
    locationRequest: LocationRequest,
    retryDelay: Duration,
    dispatcher: CoroutineDispatcher,
): Fetcher<Unit, LocationResult> =
    Fetcher.ofFlow {
        channelFlow {
            tryGetLastLocation(client)?.let { result ->
                logDebug { "Got last location $result" }
                offer(result)
            } ?: logDebug { "No last location" }
            while (!isClosedForSend) {
                stream(client, locationRequest)
                logDebug { "Stream ended. Retrying in $retryDelay" }
                delay(retryDelay.toMillis())
            }
            logDebug { "Closed location flow" }
        }.flowOn(dispatcher)
    }

private suspend fun tryGetLastLocation(client: FusedLocationProviderClient): LocationResult? =
    try {
        val available = client.locationAvailability.await().isLocationAvailable
        if (available) {
            val androidLocation = client.lastLocation.await()
            val location = androidLocation.toLocation()
            LocationResult.success(location)
        } else null
    } catch (e: SecurityException) {
        logWarn(e) { "Failed to get location." }
        LocationResult.errorMissingPermission()
    } catch (e: Exception) {
        logError(e) { "Failed to get location." }
        LocationResult.errorUnknown()
    }

@ExperimentalCoroutinesApi
private suspend fun ProducerScope<LocationResult>.stream(
    client: FusedLocationProviderClient,
    locationRequest: LocationRequest
) {
    val callback = LoadedLocationResultCallback { location ->
        val result = LocationResult.success(location)
        logDebug { "Got location from callback: $result" }
        offer(result)
    }

    // FIXME retry?
    try {
        val task = client.requestLocationUpdates(locationRequest, callback, Looper.getMainLooper())
        task.addOnFailureListener { e ->
            val result = if (e is SecurityException) {
                logWarn(e) { "Failed to get location." }
                LocationResult.errorMissingPermission()
            } else {
                logError(e) { "Failed to get location." }
                LocationResult.errorUnknown()
            }
            offer(result)
        }
        task.await()
    } catch (e: CancellationException) {
        logDebug(e) { "Cancelled" }
    } catch (e: SecurityException) {
        logWarn(e) { "Failed to get location." }
        val result = LocationResult.errorMissingPermission()
        offer(result)
    } catch (e: Exception) {
        logError(e) { "Failed to get location." }
        val result = LocationResult.errorUnknown()
        offer(result)
    } finally {
        awaitClose {
            logDebug { "Stream closed. Removing callback" }
            client.removeLocationUpdates(callback)
        }
    }
}

private class LoadedLocationResultCallback(
    private val callback: (Location) -> Unit
) : LocationCallback() {
    override fun onLocationResult(locationResult: GmsLocationResult) {
        val lastAndroidLocation: AndroidLocation? = locationResult.lastLocation
        if (lastAndroidLocation != null) {
            val location = lastAndroidLocation.toLocation()
            callback(location)
        }
    }
}

private fun AndroidLocation.toLocation(): Location = Location(latitude, longitude)

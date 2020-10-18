package se.gustavkarlsson.skylight.android.lib.location

import android.os.Looper
import androidx.annotation.RequiresPermission
import com.dropbox.android.external.store4.Fetcher
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationAvailability
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.channels.SendChannel
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
    looper: Looper,
    locationRequest: LocationRequest,
    retryDelay: Duration,
    dispatcher: CoroutineDispatcher,
): Fetcher<Unit, LocationResult> =
    Fetcher.ofFlow {
        channelFlow {
            tryLastLocation(client)
            streamUntilClosed(client, looper, locationRequest, retryDelay)
        }.flowOn(dispatcher)
    }

@ExperimentalCoroutinesApi
private suspend fun ProducerScope<LocationResult>.tryLastLocation(client: FusedLocationProviderClient) {
    val result = try {
        logDebug { "Trying to get last location" }
        val locationAvailable = client.awaitIsLocationAvailable()
        if (locationAvailable) {
            val location = client.awaitLastLocation()
            val result = LocationResult.success(location)
            logDebug { "Got last location $result" }
            result
        } else {
            logDebug { "Last location not available" }
            null
        }
    } catch (e: CancellationException) {
        logDebug(e) { "Channel cancelled" }
        null
    } catch (e: SecurityException) {
        logWarn(e) { "Failed to get last location" }
        LocationResult.errorMissingPermission()
    } catch (e: Exception) {
        logError(e) { "Failed to get last location" }
        LocationResult.errorUnknown()
    }
    if (result != null) {
        offerCatching(result)
    }
}

@RequiresPermission(anyOf = ["android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"])
private suspend fun FusedLocationProviderClient.awaitIsLocationAvailable(): Boolean =
    locationAvailability.await<LocationAvailability?>()?.isLocationAvailable ?: false

@RequiresPermission(anyOf = ["android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"])
private suspend fun FusedLocationProviderClient.awaitLastLocation(): Location = lastLocation.await().toLocation()

@ExperimentalCoroutinesApi
private suspend fun ProducerScope<LocationResult>.streamUntilClosed(
    client: FusedLocationProviderClient,
    looper: Looper,
    locationRequest: LocationRequest,
    retryDelay: Duration,
) {
    while (!isClosedForSend) {
        stream(client, looper, locationRequest)
        if (!isClosedForSend) {
            logDebug { "Stream ended prematurely. Retrying in $retryDelay" }
            delay(retryDelay.toMillis())
        } else {
            logDebug { "Stream ended and channel is closed" }
        }
    }
    logDebug { "Closing location flow" }
}

@ExperimentalCoroutinesApi
private suspend fun ProducerScope<LocationResult>.stream(
    client: FusedLocationProviderClient,
    looper: Looper,
    locationRequest: LocationRequest,
) {
    val callback = LatestLocationCallback { location ->
        val result = LocationResult.success(location)
        logDebug { "Got location from callback: $result" }
        offerCatching(result)
    }

    try {
        logDebug { "Streaming location" }
        val task = client.requestLocationUpdates(locationRequest, callback, looper)
        task.addOnFailureListener { e ->
            val result = if (e is SecurityException) {
                logWarn(e) { "Failed to get location." }
                LocationResult.errorMissingPermission()
            } else {
                logError(e) { "Failed to get location." }
                LocationResult.errorUnknown()
            }
            offerCatching(result)
        }
        task.await() // FIXME this completes prematurely. Why?
    } catch (e: CancellationException) {
        logDebug(e) { "Channel cancelled" }
    } catch (e: SecurityException) {
        logWarn(e) { "Failed to get location" }
        val result = LocationResult.errorMissingPermission()
        offerCatching(result)
    } catch (e: Exception) {
        logError(e) { "Failed to get location" }
        val result = LocationResult.errorUnknown()
        offerCatching(result)
    } finally {
        logDebug { "Stream stopped. Removing callback" }
        client.removeLocationUpdates(callback)
    }
}

private class LatestLocationCallback(
    private val onLocation: (Location) -> Unit
) : LocationCallback() {
    override fun onLocationResult(locationResult: GmsLocationResult) {
        val latestAndroidLocation: AndroidLocation? = locationResult.lastLocation
        if (latestAndroidLocation != null) {
            val location = latestAndroidLocation.toLocation()
            onLocation(location)
        }
    }
}

private fun <T> SendChannel<T>.offerCatching(element: T): Boolean =
    try {
        offer(element)
    } catch (e: Exception) {
        false
    }

private fun AndroidLocation.toLocation(): Location = Location(latitude, longitude)

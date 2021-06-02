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
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import org.threeten.bp.Duration
import se.gustavkarlsson.skylight.android.core.logging.logDebug
import se.gustavkarlsson.skylight.android.core.logging.logError
import se.gustavkarlsson.skylight.android.core.logging.logWarn
import android.location.Location as AndroidLocation
import com.google.android.gms.location.LocationResult as GmsLocationResult
import kotlinx.coroutines.channels.trySendBlocking

internal fun createLocationFetcher(
    client: FusedLocationProviderClient,
    looper: Looper,
    locationRequest: LocationRequest,
    retryDelay: Duration,
    dispatcher: CoroutineDispatcher,
): Fetcher<Unit, LocationResult> =
    Fetcher.ofFlow {
        flow {
            emitAll(lastLocation(client))
            emitAll(stream(client, looper, locationRequest, retryDelay))
        }.distinctUntilChanged()
            .flowOn(dispatcher)
    }

private fun lastLocation(client: FusedLocationProviderClient): Flow<LocationResult> =
    flow {
        try {
            logDebug { "Trying to get last location" }
            val locationAvailable = client.awaitIsLocationAvailable()
            if (locationAvailable) {
                val location = client.awaitLastLocation()
                val result = LocationResult.success(location)
                logDebug { "Got last location $result" }
                emit(result)
            } else {
                logDebug { "Last location not available" }
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: SecurityException) {
            logWarn(e) { "Failed to get last location" }
            emit(LocationResult.errorMissingPermission())
        } catch (e: Exception) {
            logError(e) { "Failed to get last location" }
            emit(LocationResult.errorUnknown())
        }
    }

@RequiresPermission(anyOf = ["android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"])
private suspend fun FusedLocationProviderClient.awaitIsLocationAvailable(): Boolean =
    locationAvailability.await<LocationAvailability?>()?.isLocationAvailable ?: false

@RequiresPermission(anyOf = ["android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"])
private suspend fun FusedLocationProviderClient.awaitLastLocation(): Location = lastLocation.await().toLocation()

private fun stream(
    client: FusedLocationProviderClient,
    looper: Looper,
    locationRequest: LocationRequest,
    retryDelay: Duration,
): Flow<LocationResult> =
    flow {
        do {
            emitAll(streamUntilError(client, looper, locationRequest))
            logDebug { "Stream ended. Retrying in $retryDelay" }
            delay(retryDelay.toMillis())
        } while (true)
    }

@OptIn(ExperimentalCoroutinesApi::class)
private fun streamUntilError(
    client: FusedLocationProviderClient,
    looper: Looper,
    locationRequest: LocationRequest,
): Flow<LocationResult> =
    callbackFlow {
        val callback = LatestLocationCallback { location ->
            val result = LocationResult.success(location)
            logDebug { "Got location update: $result" }
            sendCatching(result)
        }

        try {
            logDebug { "Requesting location updates" }
            val task = client.requestLocationUpdates(locationRequest, callback, looper)
            task.addOnFailureListener { e ->
                val result = if (e is SecurityException) {
                    logWarn(e) { "Failed to get location update" }
                    LocationResult.errorMissingPermission()
                } else {
                    logError(e) { "Failed to get location update" }
                    LocationResult.errorUnknown()
                }
                sendCatching(result)
                close(e)
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: SecurityException) {
            logWarn(e) { "Failed to request location updates" }
            val result = LocationResult.errorMissingPermission()
            sendCatching(result)
            close(e)
        } catch (e: Exception) {
            logError(e) { "Failed to request location updates" }
            val result = LocationResult.errorUnknown()
            sendCatching(result)
            close(e)
        } finally {
            awaitClose {
                logDebug { "Flow closing. Removing callback" }
                client.removeLocationUpdates(callback)
            }
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

private fun <T> SendChannel<T>.sendCatching(element: T) = trySendBlocking(element)

private fun AndroidLocation.toLocation(): Location = Location(latitude, longitude)

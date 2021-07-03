package se.gustavkarlsson.skylight.android.lib.location

import android.annotation.SuppressLint
import android.os.Looper
import androidx.annotation.RequiresPermission
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationAvailability
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.tasks.await
import org.threeten.bp.Duration
import se.gustavkarlsson.skylight.android.core.entities.Loadable
import se.gustavkarlsson.skylight.android.core.logging.logDebug
import se.gustavkarlsson.skylight.android.core.logging.logError
import se.gustavkarlsson.skylight.android.core.logging.logInfo
import se.gustavkarlsson.skylight.android.core.logging.logWarn
import se.gustavkarlsson.skylight.android.lib.permissions.Access
import se.gustavkarlsson.skylight.android.lib.permissions.Permission
import se.gustavkarlsson.skylight.android.lib.permissions.PermissionChecker
import kotlin.time.ExperimentalTime
import android.location.Location as AndroidLocation

// FIXME add logging
internal class GmsLocationProvider(
    private val client: FusedLocationProviderClient,
    private val locationRequest: LocationRequest,
    private val freshLocationRequestPriority: Int,
    private val permissionChecker: PermissionChecker,
    private val looper: Looper,
    private val streamRetryDuration: Duration,
) : LocationProvider {
    override suspend fun get(fresh: Boolean): LocationResult {
        val result = try {
            if (fresh) {
                logInfo { "Getting fresh location" }
                getFreshLocation()
            } else {
                logInfo { "Getting cached location" }
                getCachedLocation()
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: SecurityException) {
            logWarn(e) { "Failed to get location" }
            LocationResult.errorMissingPermission()
        } catch (e: Exception) {
            logError(e) { "Failed to get location" }
            LocationResult.errorUnknown()
        }
        logInfo { "Provided location: $result" }
        return result
    }

    private suspend fun getFreshLocation(): LocationResult {
        logDebug { "Checking location permission" }
        if (!hasLocationPermission()) {
            logDebug { "Location permission denied" }
            return LocationResult.errorMissingPermission()
        }
        logDebug { "Trying to get current location" }
        @SuppressLint("MissingPermission")
        val location = client.awaitCurrentLocation(freshLocationRequestPriority)
        return if (location != null) {
            logDebug { "Successfully got current location" }
            LocationResult.success(location.toLocation())
        } else {
            logWarn { "Failed to get current location" }
            LocationResult.errorUnknown()
        }
    }

    private suspend fun getCachedLocation(): LocationResult {
        logDebug { "Checking location permission" }
        if (!hasLocationPermission()) {
            logDebug { "Location permission denied" }
            return LocationResult.errorMissingPermission()
        }
        logDebug { "Checking if last location is up to date" }
        @SuppressLint("MissingPermission")
        if (!client.awaitIsLocationAvailable()) {
            logWarn { "Last location is not up to date. Trying with fresh location" }
            return getFreshLocation()
        }
        logDebug { "Last location is up to date" }
        logDebug { "Trying to get last location" }
        @SuppressLint("MissingPermission")
        val lastLocation = client.awaitLastLocation()
        if (lastLocation == null) {
            logWarn { "Failed to get last location. Trying with fresh location" }
            return getFreshLocation()
        }
        logDebug { "Successfully got last location" }
        return LocationResult.success(lastLocation)
    }

    private fun hasLocationPermission(): Boolean {
        val permissions = permissionChecker.permissions.value
        val locationPermission = permissions[Permission.Location]
        logDebug { "Location permission is $locationPermission" }
        return locationPermission == Access.Granted
    }

    @OptIn(ExperimentalTime::class)
    override fun stream(): Flow<Loadable<LocationResult>> {
        return client.streamWithPermissionCheck(permissionChecker)
            .distinctUntilChanged()
            .map { result -> Loadable.loaded(result) }
            .onEach { logInfo { "Streamed location: $it" } }
            .onStart {
                logInfo { "Streaming location" }
                emit(Loadable.loading())
                emit(Loadable.loaded(getCachedLocation()))
            }
    }

    private fun FusedLocationProviderClient.streamWithPermissionCheck(
        permissionChecker: PermissionChecker,
    ): Flow<LocationResult> {
        return permissionChecker.permissions
            .map { permissions ->
                permissions[Permission.Location] == Access.Granted
            }
            .distinctUntilChanged()
            .flatMapLatest { permissionGranted ->
                if (permissionGranted) {
                    stream(locationRequest, streamRetryDuration, looper)
                } else flowOf(LocationResult.errorMissingPermission())
            }
    }
}

@RequiresPermission(anyOf = ["android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"])
private suspend fun FusedLocationProviderClient.awaitCurrentLocation(priorityHighAccuracy: Int): AndroidLocation? {
    val cancellationSource = CancellationTokenSource()
    return try {
        getCurrentLocation(priorityHighAccuracy, cancellationSource.token).await()
    } catch (e: CancellationException) {
        cancellationSource.cancel()
        throw e
    }
}

@RequiresPermission(anyOf = ["android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"])
private suspend fun FusedLocationProviderClient.awaitIsLocationAvailable(): Boolean {
    val availability = locationAvailability.await<LocationAvailability?>()
    return availability?.isLocationAvailable == true
}

@RequiresPermission(anyOf = ["android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"])
private suspend fun FusedLocationProviderClient.awaitLastLocation(): Location? {
    val location = lastLocation.await<AndroidLocation?>()
    return location?.toLocation()
}

private fun FusedLocationProviderClient.stream(
    locationRequest: LocationRequest,
    retryDelay: Duration,
    looper: Looper,
): Flow<LocationResult> = flow {
    do {
        emitAll(streamUntilError(locationRequest, looper))
        logDebug { "Stream ended. Retrying in $retryDelay" }
        delay(retryDelay.toMillis())
    } while (true)
}

@OptIn(ExperimentalCoroutinesApi::class)
private fun FusedLocationProviderClient.streamUntilError(
    locationRequest: LocationRequest,
    looper: Looper,
): Flow<LocationResult> = callbackFlow {
    val callback = LatestLocationCallback { location ->
        val result = LocationResult.success(location)
        logDebug { "Got location update: $result" }
        sendCatching(result)
    }

    try {
        logDebug { "Requesting location updates" }
        val task = requestLocationUpdates(locationRequest, callback, looper)
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
            removeLocationUpdates(callback)
        }
    }
}

private class LatestLocationCallback(
    private val onLocation: (Location) -> Unit
) : LocationCallback() {
    override fun onLocationResult(locationResult: com.google.android.gms.location.LocationResult) {
        val latestAndroidLocation: AndroidLocation? = locationResult.lastLocation
        if (latestAndroidLocation != null) {
            val location = latestAndroidLocation.toLocation()
            onLocation(location)
        }
    }
}

private fun <T> SendChannel<T>.sendCatching(element: T) = trySendBlocking(element)

private fun AndroidLocation.toLocation(): Location = Location(latitude, longitude)

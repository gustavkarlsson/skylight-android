package se.gustavkarlsson.skylight.android.lib.location

import android.annotation.SuppressLint
import androidx.annotation.RequiresPermission
import arrow.core.left
import arrow.core.right
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationAvailability
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.threeten.bp.Duration
import se.gustavkarlsson.skylight.android.core.entities.Loadable
import se.gustavkarlsson.skylight.android.core.entities.Loaded
import se.gustavkarlsson.skylight.android.core.entities.Loading
import se.gustavkarlsson.skylight.android.core.logging.logDebug
import se.gustavkarlsson.skylight.android.core.logging.logError
import se.gustavkarlsson.skylight.android.core.logging.logInfo
import se.gustavkarlsson.skylight.android.core.logging.logWarn
import se.gustavkarlsson.skylight.android.lib.permissions.Access
import se.gustavkarlsson.skylight.android.lib.permissions.Permission
import se.gustavkarlsson.skylight.android.lib.permissions.PermissionChecker
import android.location.Location as AndroidLocation
import com.google.android.gms.location.LocationResult as GmsLocationResult

internal class GmsLocationProvider(
    private val client: FusedLocationProviderClient,
    private val locationRequest: LocationRequest,
    private val locationServiceStatusProvider: LocationServiceStatusProvider,
    private val freshLocationRequestPriority: Int,
    private val permissionChecker: PermissionChecker,
    private val streamRetryDuration: Duration,
    shareScope: CoroutineScope,
    private val dispatcher: CoroutineDispatcher,
) : LocationProvider {
    override suspend fun get(fresh: Boolean): LocationResult = withContext(dispatcher) {
        logDebug { "Checking location service availability" }
        if (!isLocationServiceEnabled()) {
            logDebug { "Location service disabled" }
            return@withContext LocationError.LocationDisabled.left()
        }
        logDebug { "Checking location permission" }
        if (!hasLocationPermission()) {
            logDebug { "Location permission denied" }
            return@withContext LocationError.NoPermission.left()
        }
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
            LocationError.NoPermission.left()
        } catch (e: Exception) {
            logError(e) { "Failed to get location" }
            LocationError.Unknown.left()
        }
        logInfo { "Provided location: $result" }
        result
    }

    private suspend fun getFreshLocation(): LocationResult {
        logDebug { "Trying to get current location" }
        @SuppressLint("MissingPermission")
        val location = client.awaitCurrentLocation(freshLocationRequestPriority)
        return if (location != null) {
            logDebug { "Successfully got current location" }
            location.toLocation().right()
        } else {
            logWarn { "Failed to get current location" }
            LocationError.Unknown.left()
        }
    }

    private suspend fun getCachedLocation(): LocationResult {
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
        return lastLocation.right()
    }

    private fun hasLocationPermission(): Boolean {
        val permissions = permissionChecker.permissions.value
        val locationPermission = permissions[Permission.Location]
        logDebug { "Location permission is $locationPermission" }
        return locationPermission == Access.Granted
    }

    private suspend fun isLocationServiceEnabled(): Boolean {
        // FIXME change to stateflow so we don't need to suspend
        val status = locationServiceStatusProvider.locationServicesStatus.first()
        logDebug { "Location service is $status" }
        return when (status) {
            LocationServiceStatus.Enabled -> true
            LocationServiceStatus.Disabled -> false
        }
    }

    override fun stream(): Flow<Loadable<LocationResult>> = sharedStream

    private val sharedStream: SharedFlow<Loadable<LocationResult>> = client.stream()
        .onStart { logInfo { "Streaming started" } }
        .onEach { logInfo { "Streamed location: $it" } }
        .flowOn(dispatcher)
        .shareIn(shareScope, SharingStarted.WhileSubscribed(), replay = 1)

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun FusedLocationProviderClient.stream(): Flow<Loadable<LocationResult>> =
        combine(
            streamLocationPermission(),
            locationServiceStatusProvider.locationServicesStatus,
        ) { permissionGranted, locationServiceStatus ->
            permissionGranted to locationServiceStatus
        }.flatMapLatest { (permissionGranted, locationServiceStatus) ->
            when (locationServiceStatus) {
                LocationServiceStatus.Enabled -> {
                    if (permissionGranted) {
                        logInfo { "Permission granted and service enabled. Starting stream" }
                        streamWithPermission()
                    } else {
                        logInfo { "Permission denied" }
                        flowOf(Loaded(LocationError.NoPermission.left()))
                    }
                }
                LocationServiceStatus.Disabled -> {
                    logInfo { "Location service disabled" }
                    flowOf(Loaded(LocationError.LocationDisabled.left()))
                }
            }
        }

    private fun streamLocationPermission(): Flow<Boolean> = permissionChecker.permissions
        .map { permissions ->
            permissions[Permission.Location] == Access.Granted
        }
        .distinctUntilChanged()

    private fun FusedLocationProviderClient.streamWithPermission(): Flow<Loadable<LocationResult>> = flow {
        emit(Loading)
        emit(Loaded(getCachedLocation()))
        emitAll(streamWithRetry(locationRequest, streamRetryDuration).map { Loaded(it) })
    }.distinctUntilChanged()
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

private fun FusedLocationProviderClient.streamWithRetry(
    locationRequest: LocationRequest,
    retryDelay: Duration,
): Flow<LocationResult> = flow {
    do {
        emitAll(streamUntilError(locationRequest))
        logDebug { "Stream ended. Retrying in $retryDelay" }
        delay(retryDelay.toMillis())
    } while (true)
}

private fun FusedLocationProviderClient.streamUntilError(
    locationRequest: LocationRequest,
): Flow<LocationResult> = callbackFlow {
    val callback = LatestLocationCallback { location ->
        val result = location.right()
        logDebug { "Got location update: $result" }
        sendCatching(result)
    }

    try {
        logDebug { "Requesting location updates" }
        // TODO Can we use a looper from a different thread?
        val task = requestLocationUpdates(locationRequest, callback, looper)
        task.addOnFailureListener { e ->
            val result = if (e is SecurityException) {
                logWarn(e) { "Failed to get location update" }
                LocationError.NoPermission.left()
            } else {
                logError(e) { "Failed to get location update" }
                LocationError.Unknown.left()
            }
            sendCatching(result)
            close(e)
        }
    } catch (e: CancellationException) {
        throw e
    } catch (e: SecurityException) {
        logWarn(e) { "Failed to request location updates" }
        val result = LocationError.NoPermission.left()
        sendCatching(result)
        close(e)
    } catch (e: Exception) {
        logError(e) { "Failed to request location updates" }
        val result = LocationError.Unknown.left()
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
    private val onLocation: (Location) -> Unit,
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

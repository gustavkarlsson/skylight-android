package se.gustavkarlsson.skylight.android.lib.location

import android.content.Context
import android.os.Looper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeoutOrNull
import org.threeten.bp.Duration
import se.gustavkarlsson.skylight.android.core.entities.Loadable
import se.gustavkarlsson.skylight.android.core.logging.logDebug
import se.gustavkarlsson.skylight.android.core.logging.logError
import se.gustavkarlsson.skylight.android.core.logging.logInfo
import se.gustavkarlsson.skylight.android.core.logging.logWarn
import android.location.Location as AndroidLocation
import com.google.android.gms.location.LocationResult as GmsLocationResult

@ExperimentalCoroutinesApi
internal class FusedLocationProviderProvider(
    context: Context,
    requestAccuracy: Int,
    requestInterval: Duration,
    private val timeout: Duration,
    retryDelay: Duration,
    dispatcher: CoroutineDispatcher
) : LocationProvider {

    private val client = FusedLocationProviderClient(context)

    private val locationRequest = LocationRequest().apply {
        priority = requestAccuracy
        interval = requestInterval.toMillis()
    }

    // FIXME share stream between subscribers.
    //  Try https://github.com/Kotlin/kotlinx.coroutines/issues/1261#issuecomment-669904086
    //  And what about other providers?

    private val stream = channelFlow<Loadable<LocationResult>> {
        offer(Loadable.loading())

        tryGetLastLocation()?.let { result ->
            logDebug { "Got last location $result" }
            offer(Loadable.loaded(result))
        } ?: logDebug { "No last location" }

        while (!this.isClosedForSend) {
            stream()
            logDebug { "Stream ended. Retrying in $retryDelay" }
            delay(retryDelay.toMillis())
        }
        logDebug { "Closed location flow" }
    }.flowOn(dispatcher)

    private suspend fun tryGetLastLocation(): LocationResult? =
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

    private suspend fun ProducerScope<Loadable<LocationResult>>.stream() {
        val callback = LoadedLocationResultCallback { location ->
            val result = LocationResult.success(location)
            logDebug { "Got location from callback: $result" }
            offer(Loadable.loaded(result))
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
                offer(Loadable.loaded(result))
            }
            task.await()
            awaitClose {
                logDebug { "Stream closed. Removing callback" }
                client.removeLocationUpdates(callback)
            }
        } catch (e: SecurityException) {
            logWarn(e) { "Failed to get location." }
            val result = LocationResult.errorMissingPermission()
            offer(Loadable.loaded(result))
        } catch (e: Exception) {
            logError(e) { "Failed to get location." }
            val result = LocationResult.errorUnknown()
            offer(Loadable.loaded(result))
        }
    }

    override suspend fun get(): LocationResult {
        val result = withTimeoutOrNull(timeout.toMillis()) {
            stream
                .filterIsInstance<Loadable.Loaded<LocationResult>>()
                .first()
                .value
        } ?: let {
            logWarn { "Timed out while getting location" }
            LocationResult.errorUnknown()
        }
        logInfo { "Provided location: $result" }
        return result
    }

    override fun stream(): Flow<Loadable<LocationResult>> =
        stream
            .distinctUntilChanged()
            .onEach { logInfo { "Streamed location: $it" } }
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

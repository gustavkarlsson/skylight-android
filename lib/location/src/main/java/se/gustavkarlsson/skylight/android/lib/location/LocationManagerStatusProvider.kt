package se.gustavkarlsson.skylight.android.lib.location

import android.annotation.SuppressLint
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Looper
import androidx.core.location.LocationManagerCompat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import se.gustavkarlsson.skylight.android.core.logging.logInfo
import se.gustavkarlsson.skylight.android.lib.permissions.Access
import se.gustavkarlsson.skylight.android.lib.permissions.Permission
import se.gustavkarlsson.skylight.android.lib.permissions.PermissionChecker
import javax.inject.Inject

internal class LocationManagerStatusProvider @Inject constructor(
    private val locationManager: LocationManager,
    private val permissionChecker: PermissionChecker,
) : LocationServiceStatusProvider {

    @OptIn(ExperimentalCoroutinesApi::class)
    @SuppressLint("MissingPermission")
    override val locationServiceStatus: Flow<LocationServiceStatus> = callbackFlow {
        val callback: () -> Unit = {
            val status = getStatus()
            logInfo { "Location service is now $status" }
            trySend(status)
        }
        callback()
        val listener = LocationServiceStatusListener(callback)
        invokeOnClose { locationManager.removeUpdates(listener) }
        permissionChecker.permissions.collectLatest { granted ->
            if (granted[Permission.Location] == Access.Granted) {
                locationManager.requestLocationUpdates(
                    LocationManager.PASSIVE_PROVIDER,
                    Long.MAX_VALUE,
                    Float.MAX_VALUE,
                    listener,
                    Looper.getMainLooper(),
                )
            } else {
                locationManager.removeUpdates(listener)
            }
        }
        error("Collect should never end")
    }.distinctUntilChanged()
        .buffer(capacity = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)

    private fun getStatus(): LocationServiceStatus {
        return if (LocationManagerCompat.isLocationEnabled(locationManager)) {
            LocationServiceStatus.Enabled
        } else {
            LocationServiceStatus.Disabled
        }
    }
}

private class LocationServiceStatusListener(
    private val callback: () -> Unit,
) : LocationListener {
    override fun onLocationChanged(location: Location) = Unit
    override fun onProviderEnabled(provider: String) = callback()
    override fun onProviderDisabled(provider: String) = callback()
}

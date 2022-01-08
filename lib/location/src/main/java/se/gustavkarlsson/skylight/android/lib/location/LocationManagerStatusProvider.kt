package se.gustavkarlsson.skylight.android.lib.location

import android.annotation.SuppressLint
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Looper
import androidx.core.location.LocationManagerCompat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.onFailure
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.distinctUntilChanged
import se.gustavkarlsson.skylight.android.core.logging.logError
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
    override val locationServicesStatus: Flow<Boolean> = callbackFlow {
        val callback = LocationServiceStatusListener(locationManager) { enabled ->
            logInfo { "Location status is now $enabled" }
            trySend(enabled).onFailure {
                logError { "Sending blocking should never fail when conflated channel" }
            }
        }
        invokeOnClose {
            locationManager.removeUpdates(callback)
        }
        permissionChecker.permissions.collectLatest { granted ->
            if (granted[Permission.Location] == Access.Granted) {
                locationManager.requestLocationUpdates(
                    LocationManager.PASSIVE_PROVIDER,
                    Long.MAX_VALUE,
                    Float.MAX_VALUE,
                    callback,
                    Looper.getMainLooper(),
                )
            } else {
                locationManager.removeUpdates(callback)
            }
        }
    }.distinctUntilChanged().conflate()
}

private class LocationServiceStatusListener(
    private val locationManager: LocationManager,
    private val onChange: (Boolean) -> Unit,
) : LocationListener {

    override fun onLocationChanged(location: Location) = Unit

    override fun onProviderEnabled(provider: String) {
        onChange()
    }

    override fun onProviderDisabled(provider: String) {
        onChange()
    }

    private fun onChange() {
        val enabled = LocationManagerCompat.isLocationEnabled(locationManager)
        onChange(enabled)
    }
}

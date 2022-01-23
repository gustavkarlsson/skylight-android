package se.gustavkarlsson.skylight.android.lib.location

import android.annotation.SuppressLint
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Looper
import androidx.core.location.LocationManagerCompat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import se.gustavkarlsson.skylight.android.core.AppScope
import se.gustavkarlsson.skylight.android.core.logging.logInfo
import se.gustavkarlsson.skylight.android.lib.permissions.Access
import se.gustavkarlsson.skylight.android.lib.permissions.Permission
import se.gustavkarlsson.skylight.android.lib.permissions.PermissionChecker
import javax.inject.Inject

@AppScope // TODO Can we make this not a singleton?
internal class LocationManagerStatusProvider @Inject constructor(
    private val locationManager: LocationManager,
    private val permissionChecker: PermissionChecker,
) : LocationServiceStatusProvider {

    private val _locationServiceStatus = MutableStateFlow(getStatus()).apply {
        logInfo { "Location service is initially $value" }
    }
    override val locationServiceStatus: StateFlow<LocationServiceStatus> = _locationServiceStatus

    @SuppressLint("MissingPermission")
    suspend fun run() {
        val callback = LocationServiceStatusListener {
            val status = getStatus()
            logInfo { "Location service is now $status" }
            _locationServiceStatus.value = status
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
        error("Collect should never end")
    }

    private fun getStatus(): LocationServiceStatus {
        return if (LocationManagerCompat.isLocationEnabled(locationManager)) {
            LocationServiceStatus.Enabled
        } else {
            LocationServiceStatus.Disabled
        }
    }
}

private class LocationServiceStatusListener(
    private val onChange: () -> Unit,
) : LocationListener {
    override fun onLocationChanged(location: Location) = Unit
    override fun onProviderEnabled(provider: String) = onChange()
    override fun onProviderDisabled(provider: String) = onChange()
}

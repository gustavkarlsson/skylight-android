package se.gustavkarlsson.skylight.android.lib.location

import android.content.Context
import android.location.LocationManager
import androidx.core.content.getSystemService
import androidx.core.location.LocationManagerCompat

internal fun isLocationEnabled(context: Context): Boolean {
    val locationManager = context.getSystemService<LocationManager>() ?: return false
    return LocationManagerCompat.isLocationEnabled(locationManager)
}

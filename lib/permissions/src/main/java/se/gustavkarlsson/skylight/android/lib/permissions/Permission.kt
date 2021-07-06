package se.gustavkarlsson.skylight.android.lib.permissions

import android.Manifest
import android.os.Build

enum class Permission(val key: String) {
    Location(
        Manifest.permission.ACCESS_FINE_LOCATION
    ),
    BackgroundLocation(
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Manifest.permission.ACCESS_BACKGROUND_LOCATION
        } else Location.key
    ),
}

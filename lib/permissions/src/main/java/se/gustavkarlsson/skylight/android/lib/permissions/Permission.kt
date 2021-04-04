package se.gustavkarlsson.skylight.android.lib.permissions

import android.Manifest
import android.os.Build

enum class Permission(val key: String, val supported: Boolean) {
    LocationFine(
        key = Manifest.permission.ACCESS_FINE_LOCATION,
        supported = true,
    ),
    BackgroundLocation(
        key = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Manifest.permission.ACCESS_BACKGROUND_LOCATION
        } else "UNSUPPORTED",
        supported = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q,
    );

    companion object {
        fun fromKey(key: String): Permission {
            return values().find { permission ->
                permission.key == key
            } ?: error("No permission for key: $key")
        }

        val supported: List<Permission> = values().filter { permission -> permission.supported }
    }
}

package se.gustavkarlsson.skylight.android.lib.permissions

import android.Manifest
import android.os.Build

sealed class Permission {
    sealed class Location : Permission() {
        object Unknown : Location()
        sealed class Granted : Location() {
            object WithBackground : Granted()
            object WithoutBackground : Granted()
        }

        object Denied : Location()
        object DeniedForever : Location()
    }

    sealed class Type {
        abstract val key: String

        object Location : Type() {
            override val key = Manifest.permission.ACCESS_COARSE_LOCATION
            val backgroundKey: String? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            } else null
        }
    }
}

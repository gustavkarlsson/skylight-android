package se.gustavkarlsson.skylight.android.lib.permissions

import android.Manifest
import android.os.Build

sealed class Permission {
    sealed class Location : Permission() {
        object Unknown : Location()
        sealed class Granted : Location() {
            object WithBackground : Granted()
            object WithoutBackground : Granted()
            // FIXME Add WithUnknownBackground ?
        }

        object Denied : Location()
        object DeniedForever : Location()
    }

    sealed class Type {
        abstract val key: String

        sealed class Location : Type() {
            override val key = Location.key
            val backgroundKey: String? = Location.backgroundKey

            object WithBackground : Location()
            object WithoutBackground : Location()
            companion object {
                const val key: String = Manifest.permission.ACCESS_COARSE_LOCATION
                val backgroundKey: String? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                } else null
            }
        }
    }
}

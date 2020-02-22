package se.gustavkarlsson.skylight.android.entities

import android.Manifest

sealed class Permission(val key: String) {
    object Location : Permission(Manifest.permission.ACCESS_FINE_LOCATION)
}

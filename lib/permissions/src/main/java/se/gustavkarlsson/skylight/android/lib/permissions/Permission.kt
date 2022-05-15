package se.gustavkarlsson.skylight.android.lib.permissions

import android.Manifest
import android.os.Build
import androidx.annotation.ChecksSdkIntAtLeast
import arrow.core.NonEmptyList
import arrow.core.nonEmptyListOf
import se.gustavkarlsson.skylight.android.core.utils.nonEmptyUnsafe

enum class Permission(
    val checkKey: String,
    val requestKeys: NonEmptyList<String>,
) {
    Location(
        checkKey = Manifest.permission.ACCESS_COARSE_LOCATION,
        requestKeys = nonEmptyListOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
        ),
    ),

    BackgroundLocation(
        checkKey = if (supportsBackgroundLocation) {
            Manifest.permission.ACCESS_BACKGROUND_LOCATION
        } else {
            Manifest.permission.ACCESS_COARSE_LOCATION
        },
        requestKeys = buildList {
            if (supportsBackgroundLocation) {
                add(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
            } else {
                add(Manifest.permission.ACCESS_COARSE_LOCATION)
            }
        }.nonEmptyUnsafe(),
    );
}

@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.Q)
private val supportsBackgroundLocation = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

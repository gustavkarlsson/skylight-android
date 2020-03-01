package se.gustavkarlsson.skylight.android.lib.permissions

import android.Manifest
import android.content.Context
import android.os.Build
import com.jakewharton.rxrelay2.BehaviorRelay
import dagger.Module
import dagger.Provides
import dagger.Reusable
import se.gustavkarlsson.skylight.android.entities.Access
import se.gustavkarlsson.skylight.android.services.PermissionChecker
import se.gustavkarlsson.skylight.android.services.PermissionRequester

@Module
class LibPermissionsModule {

    private val relay = BehaviorRelay.create<Access>()

    @Provides
    @Reusable
    internal fun locationPermissionChecker(
        context: Context
    ): PermissionChecker =
        AndroidPermissionChecker(locationPermissionKey, context, relay)

    @Provides
    @Reusable
    internal fun locationPermissionRequester(): PermissionRequester =
        RxPermissionRequester(allLocationPermissionKeys, relay)
}

private const val locationPermissionKey = Manifest.permission.ACCESS_FINE_LOCATION

// TODO Show dialog if background permission not given but notifications are on.
private val allLocationPermissionKeys = listOfNotNull(
    locationPermissionKey,
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        Manifest.permission.ACCESS_BACKGROUND_LOCATION
    } else null
)

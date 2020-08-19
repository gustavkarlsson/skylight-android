package se.gustavkarlsson.skylight.android.lib.permissions

import android.Manifest
import android.content.Context
import android.os.Build
import dagger.Module
import dagger.Provides
import dagger.Reusable
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.ConflatedBroadcastChannel

@ExperimentalCoroutinesApi
@Module
object LibPermissionsModule {

    private val channel = ConflatedBroadcastChannel(Access.Unknown)

    @Provides
    @Reusable
    internal fun locationPermissionChecker(
        context: Context
    ): PermissionChecker =
        AndroidPermissionChecker(locationPermissionKey, context, channel)

    @Provides
    @Reusable
    internal fun locationPermissionRequester(): PermissionRequester =
        RuntimePermissionRequester(
            requiredPermissionKeys = listOf(locationPermissionKey),
            extraPermissionKeys = listOfNotNull(backgroundLocationPermissionKey)
        ) { channel.offer(it) }
}

private const val locationPermissionKey = Manifest.permission.ACCESS_FINE_LOCATION

private val backgroundLocationPermissionKey =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        Manifest.permission.ACCESS_BACKGROUND_LOCATION
    } else null

// TODO Show dialog if background permission not given but notifications are on.

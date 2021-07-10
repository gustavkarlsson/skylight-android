package se.gustavkarlsson.skylight.android.lib.location

import android.content.Context
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import se.gustavkarlsson.skylight.android.core.AppScope
import se.gustavkarlsson.skylight.android.core.Global
import se.gustavkarlsson.skylight.android.core.Io
import se.gustavkarlsson.skylight.android.core.utils.minutes
import se.gustavkarlsson.skylight.android.core.utils.seconds
import se.gustavkarlsson.skylight.android.lib.permissions.PermissionChecker

@Module
object LibLocationModule {
    @Provides
    @AppScope
    internal fun locationProvider(
        context: Context,
        permissionChecker: PermissionChecker,
        @Global scope: CoroutineScope,
        @Io dispatcher: CoroutineDispatcher,
    ): LocationProvider {
        val client = LocationServices.getFusedLocationProviderClient(context)
        val requestPriority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        val locationRequest = LocationRequest.create().apply {
            priority = requestPriority
            fastestInterval = 1.minutes.toMillis()
            interval = 10.minutes.toMillis()
            maxWaitTime = 15.minutes.toMillis()
            smallestDisplacement = 200.toFloat()
        }
        return GmsLocationProvider(
            client = client,
            locationRequest = locationRequest,
            freshLocationRequestPriority = requestPriority,
            permissionChecker = permissionChecker,
            streamRetryDuration = 15.seconds,
            shareScope = scope,
            dispatcher = dispatcher,
        )
    }
}

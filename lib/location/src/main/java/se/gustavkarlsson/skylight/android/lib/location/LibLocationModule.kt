package se.gustavkarlsson.skylight.android.lib.location

import android.content.Context
import android.os.Looper
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import se.gustavkarlsson.skylight.android.core.AppScope
import se.gustavkarlsson.skylight.android.core.utils.minutes
import se.gustavkarlsson.skylight.android.core.utils.seconds
import se.gustavkarlsson.skylight.android.lib.permissions.PermissionChecker
import kotlin.time.ExperimentalTime

@Module
object LibLocationModule {
    @OptIn(
        FlowPreview::class,
        ExperimentalCoroutinesApi::class,
        ExperimentalTime::class,
    )
    @Provides
    @AppScope
    internal fun locationProvider(
        context: Context,
        permissionChecker: PermissionChecker,
    ): LocationProvider {
        val client = LocationServices.getFusedLocationProviderClient(context)
        val requestPriority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        val locationRequest = LocationRequest.create().apply {
            priority = requestPriority
            fastestInterval = 1.minutes.toMillis()
            interval = 2.minutes.toMillis()// FIXME update
            maxWaitTime = 15.minutes.toMillis()
            smallestDisplacement = 200.toFloat()
        }
        return GmsLocationProvider(
            client = client,
            locationRequest = locationRequest,
            freshLocationRequestPriority = requestPriority,
            permissionChecker = permissionChecker,
            looper = Looper.getMainLooper(),
            streamRetryDuration = 15.seconds,
        )
    }
}

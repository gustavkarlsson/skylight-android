package se.gustavkarlsson.skylight.android.lib.location

import android.content.Context
import android.location.LocationManager
import androidx.core.content.getSystemService
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import dagger.Module
import dagger.Provides
import dagger.Reusable
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
        locationServiceStatusProvider: LocationServiceStatusProvider,
        @Global globalScope: CoroutineScope,
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
            locationServiceStatusProvider = locationServiceStatusProvider,
            freshLocationRequestPriority = requestPriority,
            permissionChecker = permissionChecker,
            streamRetryDuration = 15.seconds,
            shareScope = globalScope,
            dispatcher = dispatcher,
        )
    }

    @Provides
    @Reusable
    fun locationManager(context: Context): LocationManager {
        return context.getSystemService()!!
    }

    @Provides
    @Reusable
    internal fun locationServiceStatusProvider(
        impl: LocationManagerStatusProvider,
    ): LocationServiceStatusProvider = impl
}

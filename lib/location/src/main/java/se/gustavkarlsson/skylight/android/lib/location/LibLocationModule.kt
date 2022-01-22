package se.gustavkarlsson.skylight.android.lib.location

import android.content.Context
import android.location.LocationManager
import androidx.core.content.getSystemService
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import dagger.Module
import dagger.Provides
import dagger.Reusable
import dagger.multibindings.IntoSet
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import se.gustavkarlsson.skylight.android.core.AppScope
import se.gustavkarlsson.skylight.android.core.Global
import se.gustavkarlsson.skylight.android.core.Io
import se.gustavkarlsson.skylight.android.core.ModuleStarter
import se.gustavkarlsson.skylight.android.core.utils.minutes
import se.gustavkarlsson.skylight.android.core.utils.seconds
import se.gustavkarlsson.skylight.android.lib.permissions.PermissionChecker

@Module
object LibLocationModule {

    // Why can't this be a provides function?
    private val locationRequest = LocationRequest.create().apply {
        priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        fastestInterval = 1.minutes.toMillis()
        interval = 10.minutes.toMillis()
        maxWaitTime = 15.minutes.toMillis()
        smallestDisplacement = 200.toFloat()
    }

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
        return GmsLocationProvider(
            client = client,
            locationRequest = locationRequest,
            locationServiceStatusProvider = locationServiceStatusProvider,
            freshLocationRequestPriority = locationRequest.priority,
            permissionChecker = permissionChecker,
            streamRetryDuration = 15.seconds,
            shareScope = globalScope,
            dispatcher = dispatcher,
        )
    }

    @Provides
    @Reusable
    internal fun locationManager(context: Context): LocationManager {
        return context.getSystemService()!!
    }

    @Provides
    @AppScope
    internal fun locationServiceStatusProvider(
        impl: LocationManagerStatusProvider,
    ): LocationServiceStatusProvider = impl

    @Provides
    internal fun locationSettingsResolver(): LocationSettingsResolver =
        SettingsClientLocationSettingsResolver(locationRequest)

    @Provides
    @Reusable
    @IntoSet
    internal fun moduleStarter(
        locationManagerStatusProvider: LocationManagerStatusProvider,
        @Global globalScope: CoroutineScope,
    ): ModuleStarter = LocationModuleStarter(locationManagerStatusProvider, globalScope)
}

package se.gustavkarlsson.skylight.android.lib.location

import android.content.Context
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import dagger.Module
import dagger.Provides
import dagger.Reusable
import dagger.multibindings.IntoSet
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import se.gustavkarlsson.skylight.android.core.AppScope
import se.gustavkarlsson.skylight.android.core.Global
import se.gustavkarlsson.skylight.android.core.Io
import se.gustavkarlsson.skylight.android.core.ModuleStarter
import se.gustavkarlsson.skylight.android.core.logging.logInfo
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
            freshLocationRequestPriority = requestPriority,
            permissionChecker = permissionChecker,
            streamRetryDuration = 15.seconds,
            shareScope = globalScope,
            dispatcher = dispatcher,
        )
    }

    @Provides
    @AppScope
    @LocationServiceStatus
    fun locationServiceStatusMutableStateFlow(context: Context): MutableStateFlow<Boolean> {
        val initialValue = isLocationEnabled(context)
        logInfo { "Location status is initially $initialValue" }
        return MutableStateFlow(initialValue)
    }

    @Provides
    @AppScope
    @LocationServiceStatus
    fun locationServiceStatusStateFlow(@LocationServiceStatus flow: MutableStateFlow<Boolean>): StateFlow<Boolean> = flow

    @Provides
    @Reusable
    @IntoSet
    fun moduleStarter(
        context: Context,
    ): ModuleStarter = LocationModuleStarter(context, LocationServiceStatusProvider())
}

package se.gustavkarlsson.skylight.android.lib.location

import android.content.Context
import android.location.LocationManager
import androidx.core.content.getSystemService
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority.PRIORITY_BALANCED_POWER_ACCURACY
import com.squareup.anvil.annotations.ContributesTo
import dagger.Module
import dagger.Provides
import dagger.Reusable
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import se.gustavkarlsson.skylight.android.core.AppScope
import se.gustavkarlsson.skylight.android.core.AppScopeMarker
import se.gustavkarlsson.skylight.android.core.Global
import se.gustavkarlsson.skylight.android.core.Io
import se.gustavkarlsson.skylight.android.lib.permissions.PermissionChecker
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

@Module
@ContributesTo(AppScopeMarker::class)
object LibLocationModule {

    // Why can't this be a provides function?
    private val locationRequest = LocationRequest.Builder(
        PRIORITY_BALANCED_POWER_ACCURACY,
        10.minutes.inWholeMilliseconds,
    )
        .setMinUpdateIntervalMillis(1.minutes.inWholeMilliseconds)
        .setMaxUpdateDelayMillis(15.minutes.inWholeMilliseconds)
        .setMinUpdateDistanceMeters(200.toFloat())
        .build()

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
    internal fun locationManager(context: Context): LocationManager =
        context.getSystemService()!!

    @Provides
    internal fun locationServiceStatusProvider(
        impl: LocationManagerStatusProvider,
    ): LocationServiceStatusProvider = impl

    @Provides
    internal fun locationSettingsResolver(): LocationSettingsResolver =
        SettingsClientLocationSettingsResolver(locationRequest)
}

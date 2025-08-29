package se.gustavkarlsson.skylight.android.lib.location

import android.content.Context
import android.location.LocationManager
import androidx.core.content.getSystemService
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority.PRIORITY_BALANCED_POWER_ACCURACY
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides
import me.tatarka.inject.annotations.Scope
import se.gustavkarlsson.skylight.android.core.CoreComponent
import se.gustavkarlsson.skylight.android.core.Global
import se.gustavkarlsson.skylight.android.core.Io
import se.gustavkarlsson.skylight.android.lib.permissions.PermissionChecker
import se.gustavkarlsson.skylight.android.lib.permissions.PermissionsComponent
import kotlin.annotation.AnnotationTarget.CLASS
import kotlin.annotation.AnnotationTarget.FUNCTION
import kotlin.annotation.AnnotationTarget.PROPERTY_GETTER
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

private val locationRequest: LocationRequest =
    LocationRequest.Builder(
        PRIORITY_BALANCED_POWER_ACCURACY,
        10.minutes.inWholeMilliseconds,
    )
        .setMinUpdateIntervalMillis(1.minutes.inWholeMilliseconds)
        .setMaxUpdateDelayMillis(15.minutes.inWholeMilliseconds)
        .setMinUpdateDistanceMeters(200.toFloat())
        .build()

@Component
@LocationScope
abstract class LocationComponent internal constructor(
    @Component internal val coreComponent: CoreComponent,
    @Component internal val permissionsComponent: PermissionsComponent,
) {

    abstract val locationProvider: LocationProvider

    abstract val locationSettingsResolver: LocationSettingsResolver

    abstract val locationServiceStatusProvider: LocationServiceStatusProvider

    @Provides
    @LocationScope
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
    internal fun locationManager(context: Context): LocationManager = requireNotNull(context.getSystemService()) {
        "Could not get LocationManager system service"
    }

    @Provides
    internal fun locationSettingsResolver(): LocationSettingsResolver =
        SettingsClientLocationSettingsResolver(locationRequest)

    @Provides
    internal fun locationServiceStatusProvider(impl: LocationManagerStatusProvider): LocationServiceStatusProvider =
        impl

    companion object {
        val instance: LocationComponent = LocationComponent::class.create(
            coreComponent = CoreComponent.instance,
            permissionsComponent = PermissionsComponent.instance,
        )
    }
}

@Scope
@Target(CLASS, FUNCTION, PROPERTY_GETTER)
annotation class LocationScope

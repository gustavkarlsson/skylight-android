package se.gustavkarlsson.skylight.android.lib.permissions

import android.Manifest
import android.content.Context
import com.jakewharton.rxrelay2.BehaviorRelay
import dagger.Module
import dagger.Provides
import dagger.Reusable
import org.koin.dsl.module.module
import se.gustavkarlsson.skylight.android.entities.Access
import se.gustavkarlsson.skylight.android.entities.Permission
import se.gustavkarlsson.skylight.android.services.PermissionChecker
import se.gustavkarlsson.skylight.android.services.PermissionRequester

val libPermissionsModule = module {

    val relay = BehaviorRelay.create<Access>()

    single("locationPermission") {
        Manifest.permission.ACCESS_FINE_LOCATION
    }

    single<PermissionChecker<Permission.Location>> {
        AndroidPermissionChecker(
            permission = Permission.Location,
            accessRelay = relay,
            context = get()
        )
    }

    single<PermissionRequester<Permission.Location>> {
        RxPermissionRequester(
            permission = Permission.Location,
            accessChangeConsumer = relay
        )
    }
}

@Module
class LibPermissionsModule {

    private val relay = BehaviorRelay.create<Access>()

    @Provides
    @Reusable
    internal fun locationPermissionChecker(
        context: Context
    ): PermissionChecker<Permission.Location> =
        AndroidPermissionChecker(Permission.Location, context, relay)

    @Provides
    @Reusable
    internal fun locationPermissionRequester(): PermissionRequester<Permission.Location> =
        RxPermissionRequester(Permission.Location, relay)
}

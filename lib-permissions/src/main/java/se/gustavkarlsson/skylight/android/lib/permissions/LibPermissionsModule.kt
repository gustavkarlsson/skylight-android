package se.gustavkarlsson.skylight.android.lib.permissions

import android.Manifest
import com.jakewharton.rxrelay2.BehaviorRelay
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

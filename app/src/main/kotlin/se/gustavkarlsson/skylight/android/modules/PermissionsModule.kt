package se.gustavkarlsson.skylight.android.modules

import com.tbruyelle.rxpermissions2.RxPermissions
import org.koin.dsl.module.module
import se.gustavkarlsson.skylight.android.BuildConfig
import se.gustavkarlsson.skylight.android.services.PermissionChecker
import se.gustavkarlsson.skylight.android.services_impl.AndroidPermissionChecker

val permissionsModule = module {

	single<PermissionChecker> {
		AndroidPermissionChecker(
			context = get(),
			permissionKey = get("locationPermission")
		)
	}

	scope("activity") {
		RxPermissions(get()).apply { setLogging(BuildConfig.DEBUG) }
	}

}

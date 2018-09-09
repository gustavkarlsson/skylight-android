package se.gustavkarlsson.skylight.android.modules

import org.koin.dsl.module.module
import se.gustavkarlsson.skylight.android.services.PermissionChecker
import se.gustavkarlsson.skylight.android.services_impl.AndroidPermissionChecker

val permissionsModule = module {

	single<PermissionChecker> {
		AndroidPermissionChecker(get(), get("locationPermission"))
	}

}

package se.gustavkarlsson.skylight.android.modules

import org.koin.dsl.module.module
import se.gustavkarlsson.skylight.android.services.PermissionChecker
import se.gustavkarlsson.skylight.android.test.TestPermissionChecker

val testPermissionsModule = module {

	single {
		TestPermissionChecker(true)
	}

	single<PermissionChecker>(override = true) {
		get<TestPermissionChecker>()
	}

}

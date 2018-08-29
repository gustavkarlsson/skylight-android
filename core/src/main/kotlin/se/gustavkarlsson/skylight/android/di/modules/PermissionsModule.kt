package se.gustavkarlsson.skylight.android.di.modules

import se.gustavkarlsson.skylight.android.services.PermissionChecker

interface PermissionsModule {
	val permissionChecker: PermissionChecker
}

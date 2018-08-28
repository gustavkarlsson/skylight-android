package se.gustavkarlsson.skylight.android.di.modules

import se.gustavkarlsson.skylight.android.services.providers.PermissionProvider

interface PermissionsModule {
	val permissionProvider: PermissionProvider
}

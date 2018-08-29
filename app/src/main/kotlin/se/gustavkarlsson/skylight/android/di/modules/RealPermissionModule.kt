package se.gustavkarlsson.skylight.android.di.modules

import se.gustavkarlsson.skylight.android.services.PermissionChecker
import se.gustavkarlsson.skylight.android.services_impl.AndroidPermissionChecker

class RealPermissionModule(
	private val contextModule: ContextModule
) : PermissionsModule {

	override val permissionChecker: PermissionChecker by lazy {
		AndroidPermissionChecker(contextModule.context)
	}
}

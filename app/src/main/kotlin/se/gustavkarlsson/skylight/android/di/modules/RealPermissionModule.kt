package se.gustavkarlsson.skylight.android.di.modules

import se.gustavkarlsson.skylight.android.services.providers.PermissionProvider
import se.gustavkarlsson.skylight.android.services_impl.providers.AndroidPermissionProvider

class RealPermissionModule(
	private val contextModule: ContextModule
) : PermissionsModule {

	override val permissionProvider: PermissionProvider by lazy {
		AndroidPermissionProvider(contextModule.context)
	}
}

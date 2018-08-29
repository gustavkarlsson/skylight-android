package se.gustavkarlsson.skylight.android.di.modules

import se.gustavkarlsson.skylight.android.services.RunVersionManager
import se.gustavkarlsson.skylight.android.services_impl.SharedPreferencesRunVersionManager

class RealRunVersionsModule(
	private val contextModule: ContextModule
) : RunVersionsModule {

	override val runVersionsManager: RunVersionManager by lazy {
		SharedPreferencesRunVersionManager(contextModule.context)
	}
}

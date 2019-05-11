package se.gustavkarlsson.skylight.android.modules

import org.koin.dsl.module.module
import se.gustavkarlsson.skylight.android.services.RunVersionManager
import se.gustavkarlsson.skylight.android.services_impl.SharedPreferencesRunVersionManager

val runVersionsModule = module {

	single<RunVersionManager> {
		SharedPreferencesRunVersionManager(context = get())
	}
}

package se.gustavkarlsson.skylight.android.di.modules

import se.gustavkarlsson.skylight.android.services.Settings
import se.gustavkarlsson.skylight.android.services_impl.RxPreferencesSettings

class RxSettingsModule(
	contextModule: ContextModule,
	rxSharedPreferencesModule: RxSharedPreferencesModule
) : SettingsModule {

	override val settings: Settings by lazy {
		RxPreferencesSettings(contextModule.context, rxSharedPreferencesModule.rxSharedPreferences)
	}
}

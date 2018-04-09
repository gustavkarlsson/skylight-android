package se.gustavkarlsson.skylight.android.di.modules

import com.f2prateek.rx.preferences2.RxSharedPreferences
import se.gustavkarlsson.skylight.android.services.Settings
import se.gustavkarlsson.skylight.android.services_impl.RxPreferencesSettings

class RxSettingsModule(
	contextModule: ContextModule,
	sharedPreferencesModule: SharedPreferencesModule
) : SettingsModule {

	override val rxSharedPreferences: RxSharedPreferences by lazy {
		RxSharedPreferences.create(sharedPreferencesModule.sharedPreferences)
	}

	override val settings: Settings by lazy {
		RxPreferencesSettings(contextModule.context, rxSharedPreferences)
	}
}

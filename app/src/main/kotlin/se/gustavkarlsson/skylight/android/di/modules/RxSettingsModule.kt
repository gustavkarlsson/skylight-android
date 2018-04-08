package se.gustavkarlsson.skylight.android.di.modules

import android.content.Context
import android.content.SharedPreferences
import com.f2prateek.rx.preferences2.RxSharedPreferences
import se.gustavkarlsson.skylight.android.services.Settings
import se.gustavkarlsson.skylight.android.services_impl.RxPreferencesSettings

class RxSettingsModule(context: Context, sharedPreferences: SharedPreferences) : SettingsModule {

	override val rxSharedPreferences: RxSharedPreferences by lazy {
		RxSharedPreferences.create(sharedPreferences)
	}

	override val settings: Settings by lazy {
		RxPreferencesSettings(context, rxSharedPreferences)
	}
}

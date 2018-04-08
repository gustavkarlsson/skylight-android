package se.gustavkarlsson.skylight.android.di.modules

import android.content.Context
import android.preference.PreferenceManager
import com.f2prateek.rx.preferences2.RxSharedPreferences
import se.gustavkarlsson.skylight.android.services.Settings
import se.gustavkarlsson.skylight.android.services_impl.RxPreferencesSettings

class RxSettingsModule(context: Context) : SettingsModule {

	override val rxSharedPreferences: RxSharedPreferences by lazy {
		val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
		RxSharedPreferences.create(sharedPreferences)
	}

	override val settings: Settings by lazy {
		RxPreferencesSettings(context, rxSharedPreferences)
	}
}

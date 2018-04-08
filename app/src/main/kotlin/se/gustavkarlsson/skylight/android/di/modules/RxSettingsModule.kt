package se.gustavkarlsson.skylight.android.di.modules

import android.content.Context
import android.preference.PreferenceManager
import com.f2prateek.rx.preferences2.RxSharedPreferences
import se.gustavkarlsson.skylight.android.services.Settings
import se.gustavkarlsson.skylight.android.services_impl.RxPreferencesSettings

class RxSettingsModule(context: Context) : SettingsModule {

	override val settings: Settings by lazy {
		val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
		val rxSharedPreferences = RxSharedPreferences.create(sharedPreferences)
		RxPreferencesSettings(context, rxSharedPreferences)
	}
}

package se.gustavkarlsson.skylight.android.di.modules

import com.f2prateek.rx.preferences2.RxSharedPreferences
import se.gustavkarlsson.skylight.android.services.Settings

interface SettingsModule {
	val rxSharedPreferences: RxSharedPreferences
	val settings: Settings
}

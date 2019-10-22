package se.gustavkarlsson.skylight.android.lib.settings

import android.preference.PreferenceManager
import com.f2prateek.rx.preferences2.RxSharedPreferences
import org.koin.dsl.module.module

val libSettingsModule = module {

	single<DevelopSettings> {
		RxPreferencesDevelopSettings(
			context = get(),
			rxSharedPreferences = get()
		)
	}

	single {
		PreferenceManager.getDefaultSharedPreferences(get())
	}

	single {
		RxSharedPreferences.create(get())
	}

	single<Settings> {
		RxPreferencesSettings(
			rxSharedPreferences = get(),
			placeRepository = get()
		)
	}
}

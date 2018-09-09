package se.gustavkarlsson.skylight.android.modules

import android.preference.PreferenceManager
import com.f2prateek.rx.preferences2.RxSharedPreferences
import org.koin.dsl.module.module
import se.gustavkarlsson.skylight.android.services.Settings
import se.gustavkarlsson.skylight.android.services_impl.RxPreferencesSettings

val settingsModule = module {

	single {
		PreferenceManager.getDefaultSharedPreferences(get())
	}

	single {
		RxSharedPreferences.create(get())
	}

	single<Settings> {
		RxPreferencesSettings(get(), get())
	}

}

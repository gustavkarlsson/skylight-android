package se.gustavkarlsson.skylight.android.feature.settings

import android.preference.PreferenceManager
import com.f2prateek.rx.preferences2.RxSharedPreferences
import org.koin.dsl.module.module
import se.gustavkarlsson.skylight.android.ModuleStarter
import se.gustavkarlsson.skylight.android.feature.base.Destination
import se.gustavkarlsson.skylight.android.feature.base.DestinationRegistry
import se.gustavkarlsson.skylight.android.services.DevelopSettings
import se.gustavkarlsson.skylight.android.services.Settings

val settingsModule = module {

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
			context = get(),
			rxSharedPreferences = get()
		)
	}

	single("preferences") {
		@Suppress("ConstantConditionIf")
		if (BuildConfig.DEVELOP) {
			listOf(R.xml.preferences, R.xml.develop_preferences)
		} else {
			listOf(R.xml.preferences)
		}
	}

	single<ModuleStarter>("settings") {
		object : ModuleStarter {
			override fun start() {
				val destination = Destination("settings", 0, true) { id ->
					if (id == "settings")
						SettingsFragment()
					else
						null
				}
				get<DestinationRegistry>().register(destination)
			}
		}
	}
}

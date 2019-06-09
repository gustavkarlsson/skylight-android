package se.gustavkarlsson.skylight.android.feature.settings

import org.koin.dsl.module.module
import se.gustavkarlsson.skylight.android.ModuleStarter
import se.gustavkarlsson.skylight.android.lib.ui.Destination
import se.gustavkarlsson.skylight.android.lib.ui.DestinationRegistry

val featureSettingsModule = module {

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
				val destination = Destination(0) { id ->
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

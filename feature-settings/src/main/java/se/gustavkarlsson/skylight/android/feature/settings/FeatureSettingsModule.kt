package se.gustavkarlsson.skylight.android.feature.settings

import androidx.fragment.app.Fragment
import org.koin.dsl.module.module
import se.gustavkarlsson.skylight.android.ModuleStarter
import se.gustavkarlsson.skylight.android.lib.navigation.FragmentFactory
import se.gustavkarlsson.skylight.android.lib.navigation.FragmentFactoryRegistry

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
				val fragmentFactory = object : FragmentFactory {
					override fun createFragment(name: String): Fragment? =
						if (name == "settings") SettingsFragment()
						else null
				}
				get<FragmentFactoryRegistry>().register(fragmentFactory)
			}
		}
	}
}

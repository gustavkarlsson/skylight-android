package se.gustavkarlsson.skylight.android.feature.settings

import android.annotation.SuppressLint
import androidx.fragment.app.Fragment
import org.koin.dsl.module.module
import se.gustavkarlsson.skylight.android.ModuleStarter
import se.gustavkarlsson.skylight.android.lib.navigation.FragmentFactory
import se.gustavkarlsson.skylight.android.lib.navigation.FragmentFactoryRegistry
import se.gustavkarlsson.skylight.android.services.Analytics
import se.gustavkarlsson.skylight.android.services.Settings

val featureSettingsModule = module {

	single<ModuleStarter>("settings") {
		val analytics = get<Analytics>()
		val settings = get<Settings>()
		val factoryRegistry = get<FragmentFactoryRegistry>()
		object : ModuleStarter {
			@SuppressLint("CheckResult")
			override fun start() {
				val fragmentFactory = object : FragmentFactory {
					override fun createFragment(name: String): Fragment? =
						if (name == "settings") SettingsFragment()
						else null
				}
				factoryRegistry.register(fragmentFactory)
				getTriggerLevels(settings)
					.subscribe { (min, max) ->
						analytics.setProperty("trigger_lvl_min", min)
						analytics.setProperty("trigger_lvl_max", max)
					}
			}
		}
	}
}

private fun getTriggerLevels(settings: Settings) =
	settings
		.notificationTriggerLevels
		.map { it.unzip().second }
		.map { triggerLevels ->
			val min = triggerLevels.minBy { it?.ordinal ?: Int.MAX_VALUE }
			val max = triggerLevels.maxBy { it?.ordinal ?: Int.MAX_VALUE }
			min to max
		}
		.distinctUntilChanged()

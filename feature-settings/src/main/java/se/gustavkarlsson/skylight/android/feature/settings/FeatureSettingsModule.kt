package se.gustavkarlsson.skylight.android.feature.settings

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers
import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module
import se.gustavkarlsson.skylight.android.ModuleStarter
import se.gustavkarlsson.skylight.android.lib.navigation.FragmentFactory
import se.gustavkarlsson.skylight.android.lib.navigation.FragmentFactoryRegistry
import se.gustavkarlsson.skylight.android.services.Analytics
import se.gustavkarlsson.skylight.android.services.Settings

val featureSettingsModule = module {

	viewModel {
		SettingsViewModel(get())
	}

	single<ModuleStarter>("settings") {
		val analytics = get<Analytics>()
		val settings = get<Settings>()
		val factoryRegistry = get<FragmentFactoryRegistry>()
		val oldSharedPreferencesFileName = "FIXME"
		val sharedPrefs =
			get<Context>().getSharedPreferences(oldSharedPreferencesFileName, Context.MODE_PRIVATE)
		object : ModuleStarter {
			@SuppressLint("CheckResult")
			override fun start() {
				factoryRegistry.register(SettingsFragmentFactory)
				getTriggerLevels(settings)
					.subscribe { (min, max) ->
						analytics.setProperty("trigger_lvl_min", min)
						analytics.setProperty("trigger_lvl_max", max)
					}
				clearOldSharedPreferences(sharedPrefs, oldSharedPreferencesFileName)
					.subscribe()
			}
		}
	}
}

private object SettingsFragmentFactory : FragmentFactory {
	override fun createFragment(name: String): Fragment? =
		if (name == "settings") SettingsFragment()
		else null
}

private fun getTriggerLevels(settings: Settings) =
	settings
		.streamNotificationTriggerLevels()
		.map { it.unzip().second }
		.map { triggerLevels ->
			val min = triggerLevels.minBy { it.ordinal }
			val max = triggerLevels.maxBy { it.ordinal }
			min to max
		}
		.distinctUntilChanged()


private fun clearOldSharedPreferences(sharedPrefs: SharedPreferences, fileName: String) =
	Completable
		.fromRunnable {
			sharedPrefs.edit(commit = true) {
				clear()
			}
			// FIXME delete file
		}
		.subscribeOn(Schedulers.io())

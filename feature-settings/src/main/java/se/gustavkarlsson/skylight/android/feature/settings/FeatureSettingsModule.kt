package se.gustavkarlsson.skylight.android.feature.settings

import android.annotation.SuppressLint
import android.content.Context
import android.preference.PreferenceManager
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import io.reactivex.rxkotlin.toObservable
import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module
import se.gustavkarlsson.skylight.android.ModuleStarter
import se.gustavkarlsson.skylight.android.lib.navigation.FragmentFactory
import se.gustavkarlsson.skylight.android.lib.navigation.FragmentFactoryRegistry
import se.gustavkarlsson.skylight.android.services.Analytics
import se.gustavkarlsson.skylight.android.services.PlacesRepository
import se.gustavkarlsson.skylight.android.services.Settings

val featureSettingsModule = module {

    viewModel {
        SettingsViewModel(get())
    }

    single<ModuleStarter>("settings") {
        val analytics = get<Analytics>()
        val placesRepository = get<PlacesRepository>()
        val settings = get<Settings>()
        val factoryRegistry = get<FragmentFactoryRegistry>()
        val context = get<Context>()
        object : ModuleStarter {
            @SuppressLint("CheckResult")
            override fun start() {
                factoryRegistry.register(SettingsFragmentFactory)
                clearSettingsForDeletedPlaces(placesRepository, settings)
                getTriggerLevels(settings)
                    .subscribe { (min, max) ->
                        analytics.setProperty("trigger_lvl_min", min)
                        analytics.setProperty("trigger_lvl_max", max)
                    }
                clearOldSharedPreferences(context)
            }
        }
    }
}

private object SettingsFragmentFactory : FragmentFactory {
    override fun createFragment(name: String): Fragment? =
        if (name == "settings") SettingsFragment()
        else null
}

@SuppressLint("CheckResult")
private fun clearSettingsForDeletedPlaces(placesRepository: PlacesRepository, settings: Settings) {
    placesRepository.stream()
        .buffer(2, 1)
        .flatMap { (old, new) ->
            val remaining = old - new
            remaining.toObservable()
        }
        .subscribe(settings::clearNotificationTriggerLevel)
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

private fun clearOldSharedPreferences(context: Context) =
    PreferenceManager.getDefaultSharedPreferences(context)
        .edit {
            putString("pref_notifications_key", null)
            putString("pref_trigger_level_key", null)
        }

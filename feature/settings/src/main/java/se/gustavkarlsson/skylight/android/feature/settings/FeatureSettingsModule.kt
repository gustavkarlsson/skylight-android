package se.gustavkarlsson.skylight.android.feature.settings

import android.annotation.SuppressLint
import android.content.Context
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoSet
import io.reactivex.rxkotlin.toObservable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.rx2.asObservable
import se.gustavkarlsson.skylight.android.core.AppScope
import se.gustavkarlsson.skylight.android.core.ModuleStarter
import se.gustavkarlsson.skylight.android.lib.analytics.Analytics
import se.gustavkarlsson.skylight.android.lib.places.PlacesRepository
import se.gustavkarlsson.skylight.android.lib.settings.Settings

@Module
object FeatureSettingsModule {

    @ExperimentalCoroutinesApi
    @Provides
    @AppScope
    @IntoSet
    fun moduleStarter(
        context: Context,
        settings: Settings,
        placesRepository: PlacesRepository,
        analytics: Analytics
    ): ModuleStarter =
        object : ModuleStarter {
            @SuppressLint("CheckResult")
            override fun start(scope: CoroutineScope) {
                clearSettingsForDeletedPlaces(placesRepository, settings)
                scope.launch {
                    getTriggerLevels(settings)
                        .collect { (min, max) ->
                            analytics.setProperty("trigger_lvl_min", min)
                            analytics.setProperty("trigger_lvl_max", max)
                        }
                }
                clearOldSharedPreferences(context)
            }
        }
}

@ExperimentalCoroutinesApi
@SuppressLint("CheckResult")
private fun clearSettingsForDeletedPlaces(placesRepository: PlacesRepository, settings: Settings) {
    placesRepository.stream().asObservable()
        .buffer(2, 1)
        .flatMap { (old, new) ->
            val remaining = old - new
            remaining.toObservable()
        }
        .subscribe(settings::clearNotificationTriggerLevel)
}

@ExperimentalCoroutinesApi
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

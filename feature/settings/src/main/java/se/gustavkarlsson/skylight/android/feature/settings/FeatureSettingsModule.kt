package se.gustavkarlsson.skylight.android.feature.settings

import android.annotation.SuppressLint
import android.content.Context
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoSet
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.launch
import se.gustavkarlsson.skylight.android.core.AppScope
import se.gustavkarlsson.skylight.android.core.ModuleStarter
import se.gustavkarlsson.skylight.android.lib.analytics.Analytics
import se.gustavkarlsson.skylight.android.lib.places.PlacesRepository
import se.gustavkarlsson.skylight.android.lib.settings.Settings

@Module
object FeatureSettingsModule {

    @FlowPreview
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
                scope.launch {
                    getRemovedPlaces(placesRepository)
                        .collect {
                            settings.clearNotificationTriggerLevel(it)
                        }
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

@FlowPreview
@ExperimentalCoroutinesApi
@SuppressLint("CheckResult")
private fun getRemovedPlaces(placesRepository: PlacesRepository) =
    placesRepository.stream()
        .windowed(2)
        .flatMapConcat { (old, new) ->
            val remaining = old - new
            remaining.asFlow()
        }

@ExperimentalCoroutinesApi
private fun getTriggerLevels(settings: Settings) =
    settings
        .streamNotificationTriggerLevels()
        .map { it.unzip().second }
        .map { triggerLevels ->
            val min = triggerLevels.minByOrNull { it.ordinal }
            val max = triggerLevels.maxByOrNull { it.ordinal }
            min to max
        }
        .distinctUntilChanged()

private fun clearOldSharedPreferences(context: Context) =
    PreferenceManager.getDefaultSharedPreferences(context)
        .edit {
            putString("pref_notifications_key", null)
            putString("pref_trigger_level_key", null)
        }

// TODO Replace with built-in once available
@ExperimentalCoroutinesApi
private fun <T> Flow<T>.windowed(size: Int): Flow<List<T>> {
    require(size > 0) { "Requested size $size is non-positive." }
    return scan(emptyList<T>()) { oldItems, newItem ->
        oldItems.takeLast(size - 1) + newItem
    }.filter { it.size == size }
}

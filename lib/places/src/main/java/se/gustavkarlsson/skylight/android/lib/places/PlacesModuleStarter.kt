package se.gustavkarlsson.skylight.android.lib.places

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import se.gustavkarlsson.skylight.android.core.ModuleStarter
import se.gustavkarlsson.skylight.android.lib.analytics.Analytics

internal class PlacesModuleStarter(
    private val placesRepository: PlacesRepository,
    private val analytics: Analytics,
    private val scope: CoroutineScope,
) : ModuleStarter {
    override fun start() {
        scope.launch {
            updateAnalyticsPlacesCount()
        }
    }

    private suspend fun updateAnalyticsPlacesCount() {
        placesRepository.stream()
            .map { it.count() }
            .distinctUntilChanged()
            .collect { placesCount ->
                analytics.setProperty("places_count", placesCount)
            }
    }
}

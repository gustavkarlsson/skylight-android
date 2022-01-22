package se.gustavkarlsson.skylight.android.lib.places

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import se.gustavkarlsson.skylight.android.core.Global
import se.gustavkarlsson.skylight.android.core.ModuleStarter
import se.gustavkarlsson.skylight.android.lib.analytics.Analytics
import javax.inject.Inject

internal class PlacesModuleStarter @Inject constructor(
    private val placesRepository: PlacesRepository,
    private val analytics: Analytics,
    @Global private val scope: CoroutineScope,
) : ModuleStarter {
    override suspend fun start() {
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

package se.gustavkarlsson.skylight.android.lib.location

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import se.gustavkarlsson.skylight.android.core.ModuleStarter

internal class LocationModuleStarter(
    private val locationManagerStatusProvider: LocationManagerStatusProvider,
    private val scope: CoroutineScope,
) : ModuleStarter {
    override suspend fun start() {
        scope.launch {
            locationManagerStatusProvider.run()
        }
    }
}

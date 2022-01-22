package se.gustavkarlsson.skylight.android.lib.location

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import se.gustavkarlsson.skylight.android.core.Global
import se.gustavkarlsson.skylight.android.core.ModuleStarter
import javax.inject.Inject

internal class LocationModuleStarter @Inject constructor(
    private val locationManagerStatusProvider: LocationManagerStatusProvider,
    @Global private val scope: CoroutineScope,
) : ModuleStarter {
    override suspend fun start() {
        scope.launch {
            locationManagerStatusProvider.run()
        }
    }
}

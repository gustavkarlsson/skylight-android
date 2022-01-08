package se.gustavkarlsson.skylight.android.lib.location

import android.content.Context
import android.content.IntentFilter
import android.location.LocationManager
import se.gustavkarlsson.skylight.android.core.ModuleStarter

internal class LocationModuleStarter(
    private val context: Context,
    private val provider: LocationServiceStatusProvider,
) : ModuleStarter {
    override suspend fun start() {
        val filter = IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION)
        context.registerReceiver(provider, filter)
    }
}

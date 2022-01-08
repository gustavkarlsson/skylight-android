package se.gustavkarlsson.skylight.android.lib.location

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import se.gustavkarlsson.skylight.android.core.logging.logInfo
import javax.inject.Inject

internal class LocationServiceStatusProvider @Inject constructor() : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == LocationManager.PROVIDERS_CHANGED_ACTION) {
            val locationServicesStatusFlow = LocationComponent.instance.mutableLocationServiceStatus()
            val enabled = isLocationEnabled(context)
            val updated = locationServicesStatusFlow.compareAndSet(!enabled, enabled)
            if (updated) {
                logInfo { "Location status is now $enabled" }
            }
        }
    }
}

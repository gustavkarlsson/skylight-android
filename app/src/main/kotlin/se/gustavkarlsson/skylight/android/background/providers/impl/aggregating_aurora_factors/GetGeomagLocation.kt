package se.gustavkarlsson.skylight.android.background.providers.impl.aggregating_aurora_factors

import android.location.Location
import android.util.Log
import se.gustavkarlsson.skylight.android.background.providers.GeomagLocationProvider
import se.gustavkarlsson.skylight.android.models.factors.GeomagLocation
import java.util.concurrent.Callable
import java.util.concurrent.TimeoutException

class GetGeomagLocation(
		private val provider: GeomagLocationProvider,
		private val location: Location
) : ErrorHandlingTask<GeomagLocation> {

    override val callable: Callable<GeomagLocation>
        get() = Callable { this.call() }

    private fun call(): GeomagLocation {
        Log.i(TAG, "Getting geomagnetic location...")
        val geomagLocation = provider.getGeomagLocation(location.latitude, location.longitude)
        Log.d(TAG, "Geomagnetic location is: " + geomagLocation)
        return geomagLocation
    }

    override fun handleInterruptedException(e: InterruptedException): GeomagLocation {
        return GeomagLocation()
    }

    override fun handleThrowable(e: Throwable): GeomagLocation {
        return GeomagLocation()
    }

    override fun handleTimeoutException(e: TimeoutException): GeomagLocation {
        return GeomagLocation()
    }

    companion object {
        private val TAG = GetGeomagLocation::class.java.simpleName
    }
}

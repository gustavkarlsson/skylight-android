package se.gustavkarlsson.skylight.android.services_impl.providers.aggregating_aurora_factors

import android.location.Location
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.debug
import org.jetbrains.anko.info
import se.gustavkarlsson.skylight.android.services.providers.GeomagLocationProvider
import se.gustavkarlsson.skylight.android.entities.GeomagLocation
import java.util.concurrent.Callable
import java.util.concurrent.TimeoutException

class GetGeomagLocation(
	private val provider: GeomagLocationProvider,
	private val location: Location
) : ErrorHandlingTask<GeomagLocation>, AnkoLogger {

    override val callable: Callable<GeomagLocation>
        get() = Callable { this.call() }

    private fun call(): GeomagLocation {
        info("Getting geomagnetic location...")
        val geomagLocation = provider.getGeomagLocation(location.latitude, location.longitude)
        debug("Geomagnetic location is: $geomagLocation")
        return geomagLocation
    }

    override fun handleInterruptedException(e: InterruptedException) = GeomagLocation()

    override fun handleThrowable(e: Throwable) = GeomagLocation()

    override fun handleTimeoutException(e: TimeoutException) = GeomagLocation()
}

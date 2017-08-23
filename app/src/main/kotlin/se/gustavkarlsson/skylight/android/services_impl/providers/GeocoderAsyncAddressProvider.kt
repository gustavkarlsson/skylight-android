package se.gustavkarlsson.skylight.android.services_impl.providers

import android.location.Address
import android.location.Geocoder
import dagger.Reusable
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.warn
import se.gustavkarlsson.skylight.android.services.providers.AsyncAddressProvider
import se.gustavkarlsson.skylight.android.dagger.CACHED_THREAD_POOL_NAME
import java.io.IOException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Future
import java.util.concurrent.FutureTask
import javax.inject.Inject
import javax.inject.Named

@Reusable
class GeocoderAsyncAddressProvider
@Inject
constructor(
		private val geocoder: Geocoder,
		@param:Named(CACHED_THREAD_POOL_NAME) private val cachedThreadPool: ExecutorService
) : AsyncAddressProvider, AnkoLogger {

    override fun execute(latitude: Double, longitude: Double): Future<Address?> {
        val getAddressTask = FutureTask { getAddress(latitude, longitude) }
        cachedThreadPool.execute(getAddressTask)
        return getAddressTask
    }

	private fun getAddress(latitude: Double, longitude: Double): Address? {
		try {
			val addresses = geocoder.getFromLocation(latitude, longitude, 1)
			return addresses.firstOrNull()
		} catch (e: IOException) {
			warn("Failed to perform reverse geocoding", e)
			return null
		}
	}
}
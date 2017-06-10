package se.gustavkarlsson.skylight.android.background.providers.impl

import android.location.Address
import android.location.Geocoder
import android.util.Log
import dagger.Reusable
import se.gustavkarlsson.skylight.android.background.providers.AsyncAddressProvider
import se.gustavkarlsson.skylight.android.dagger.Names.CACHED_THREAD_POOL_NAME
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
) : AsyncAddressProvider {

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
			Log.w(TAG, "Failed to perform reverse geocoding", e)
			return null
		}
	}

	companion object {
        private val TAG = GeocoderAsyncAddressProvider::class.java.simpleName
    }
}

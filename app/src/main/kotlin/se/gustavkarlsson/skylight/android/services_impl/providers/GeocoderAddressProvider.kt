package se.gustavkarlsson.skylight.android.services_impl.providers

import android.location.Address
import android.location.Geocoder
import dagger.Reusable
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.warn
import se.gustavkarlsson.skylight.android.services.providers.AddressProvider
import java.io.IOException
import javax.inject.Inject

@Reusable
class GeocoderAddressProvider
@Inject
constructor(
		private val geocoder: Geocoder
) : AddressProvider, AnkoLogger {

	// TODO Use coroutines
    suspend override fun getAddress(latitude: Double, longitude: Double): Address? {
        return try {
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
            addresses.firstOrNull()
        } catch (e: IOException) {
            warn("Failed to perform reverse geocoding", e)
            null
        }
    }
}

package se.gustavkarlsson.skylight.android.services_impl.providers

import android.location.Geocoder
import dagger.Reusable
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.warn
import se.gustavkarlsson.skylight.android.services.Location
import se.gustavkarlsson.skylight.android.services.providers.LocationNameProvider
import java.io.IOException
import javax.inject.Inject

@Reusable
class GeocoderLocationNameProvider
@Inject
constructor(
		private val geocoder: Geocoder
) : LocationNameProvider, AnkoLogger {

    override fun getLocationName(location: Location): String? {
        return try {
            val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
            addresses.firstOrNull()?.locality
        } catch (e: IOException) {
            warn("Failed to perform reverse geocoding", e)
            null
        }
    }
}

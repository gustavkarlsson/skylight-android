package se.gustavkarlsson.skylight.android.services_impl.providers

import android.location.Geocoder
import com.hadisatrio.optional.Optional
import dagger.Reusable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import se.gustavkarlsson.skylight.android.entities.Location
import se.gustavkarlsson.skylight.android.services.providers.LocationNameProvider
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

@Reusable
class GeocoderLocationNameProvider
@Inject
constructor(
	private val geocoder: Geocoder
) : LocationNameProvider {

	override fun get(location: Single<Location>): Single<Optional<String>> {
		return location
			.observeOn(Schedulers.io())
			.map {
				try {
					val addresses = geocoder.getFromLocation(it.latitude, it.longitude, 1)
					Optional.ofNullable<String>(addresses.firstOrNull()?.locality)
				} catch (e: IOException) {
					Timber.w(e, "Failed to perform reverse geocoding")
					Optional.absent<String>()
				}
			}
	}
}

package se.gustavkarlsson.skylight.android.locationname

import android.location.Geocoder
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import se.gustavkarlsson.koptional.Absent
import se.gustavkarlsson.koptional.Optional
import se.gustavkarlsson.koptional.optionalOf
import se.gustavkarlsson.skylight.android.entities.Location
import se.gustavkarlsson.skylight.android.services.providers.LocationNameProvider
import timber.log.Timber

internal class GeocoderLocationNameProvider(
	private val geocoder: Geocoder
) : LocationNameProvider {

	override fun get(location: Single<Optional<Location>>): Single<Optional<String>> {
		return location
			.observeOn(Schedulers.io())
			.map {
				it.value?.let {
					try {
						val addresses = geocoder.getFromLocation(it.latitude, it.longitude, 1)
						optionalOf(addresses.firstOrNull()?.locality)
					} catch (e: Throwable) {
						Timber.w(e, "Failed to perform reverse geocoding")
						Absent
					}
				} ?: Absent
			}
			.doOnSuccess { Timber.i("Provided location name: %s", it.value) }
	}
}

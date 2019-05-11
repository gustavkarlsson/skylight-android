package se.gustavkarlsson.skylight.android.locationname

import android.location.Geocoder
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import org.threeten.bp.Duration
import se.gustavkarlsson.koptional.Absent
import se.gustavkarlsson.koptional.Optional
import se.gustavkarlsson.koptional.optionalOf
import se.gustavkarlsson.skylight.android.entities.Location
import se.gustavkarlsson.skylight.android.extensions.delay
import se.gustavkarlsson.skylight.android.services.providers.LocationNameProvider
import timber.log.Timber

internal class GeocoderLocationNameProvider(
	private val geocoder: Geocoder,
	private val retryDelay: Duration
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

	override fun stream(locations: Flowable<Optional<Location>>): Flowable<Optional<String>> =
		locations
			.switchMap {
				get(Single.just(it))
					.retryWhen { it.delay(retryDelay) }
					.toFlowable()
			}
			.distinctUntilChanged()
			.doOnNext { Timber.i("Streamed location name: %s", it.value) }
			.replay(1)
			.refCount()
}

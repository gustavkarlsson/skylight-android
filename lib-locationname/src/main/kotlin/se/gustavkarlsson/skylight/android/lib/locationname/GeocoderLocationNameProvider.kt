package se.gustavkarlsson.skylight.android.lib.locationname

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

	private fun getSingleName(location: Single<Optional<Location>>): Single<Optional<String>> =
		location
			.observeOn(Schedulers.io())
			.flatMap { (location) ->
				location?.let {
					try {
						val addresses = geocoder.getFromLocation(it.latitude, it.longitude, 1)
						Single.just(optionalOf(addresses.firstOrNull()?.locality))
					} catch (e: Throwable) {
						Single.error<Optional<String>>(e)
					}
				} ?: Single.just(Absent)
			}
			.doOnError { Timber.w(it, "Failed to perform reverse geocoding") }

	override fun get(location: Single<Optional<Location>>): Single<Optional<String>> =
		getSingleName(location)
			.onErrorReturnItem(Absent)
			.doOnSuccess { Timber.i("Provided location name: %s", it.value) }

	override fun stream(locations: Flowable<Optional<Location>>): Flowable<Optional<String>> =
		locations
			.switchMap { location ->
				getSingleName(Single.just(location))
					.toFlowable()
					.onErrorResumeNext { it: Throwable ->
						Flowable.error<Optional<String>>(it)
							.startWith(Absent)
					}
					.retryWhen { it.delay(retryDelay) }
			}
			.distinctUntilChanged()
			.doOnNext { Timber.i("Streamed location name: %s", it.value) }
			.replay(1)
			.refCount()
}

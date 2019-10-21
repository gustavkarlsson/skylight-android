package se.gustavkarlsson.skylight.android.lib.reversegeocoder

import android.location.Geocoder
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import org.threeten.bp.Duration
import se.gustavkarlsson.koptional.Absent
import se.gustavkarlsson.koptional.Optional
import se.gustavkarlsson.koptional.optionalOf
import se.gustavkarlsson.skylight.android.entities.Loadable
import se.gustavkarlsson.skylight.android.entities.LocationResult
import se.gustavkarlsson.skylight.android.extensions.delay
import timber.log.Timber
import java.io.IOException

internal class AndroidReverseGeocoder(
	private val geocoder: Geocoder,
	private val retryDelay: Duration
) : ReverseGeocoder {

	override fun get(location: Single<LocationResult>): Single<Optional<String>> =
		location
			.flatMap(::getSingleName)
			.map { reverseGeocodingResult ->
				when (reverseGeocodingResult) {
					is ReverseGeocodingResult.Success -> optionalOf(reverseGeocodingResult.name)
					is ReverseGeocodingResult.Failure -> Absent
				}
			}
			.doOnSuccess { Timber.i("Provided location name: %s", it.value) }

	override fun stream(
		locations: Flowable<Loadable<LocationResult>>
	): Flowable<Loadable<String?>> =
		locations
			.switchMap { loadableLocationResult ->
				when (loadableLocationResult) {
					Loadable.Loading -> Flowable.just(Loadable.Loading)
					is Loadable.Loaded -> {
						getSingleName(loadableLocationResult.value)
							.toFlowable()
							.flatMap { reverseGeocodingResult ->
								when (reverseGeocodingResult) {
									is ReverseGeocodingResult.Success ->
										Flowable.just(Loadable.Loaded(reverseGeocodingResult.name))
									ReverseGeocodingResult.Failure.Location ->
										Flowable.just(Loadable.Loaded(null))
									is ReverseGeocodingResult.Failure.Io ->
										Flowable.concat(
											Flowable.just(Loadable.Loaded(null)),
											Flowable.error(reverseGeocodingResult.exception)
										)
								}
							}
							.retryWhen { it.delay(retryDelay) }
					}
				}
			}
			.distinctUntilChanged()
			.doOnNext { Timber.i("Streamed location name: %s", it) }

	private fun getSingleName(locationResult: LocationResult): Single<ReverseGeocodingResult> =
		Single.fromCallable {
			when (locationResult) {
				is LocationResult.Success -> {
					val actualLocation = locationResult.location
					try {
						val addresses = geocoder.getFromLocation(
							actualLocation.latitude,
							actualLocation.longitude,
							1
						)
						ReverseGeocodingResult.Success(addresses.firstOrNull()?.locality)
					} catch (e: IOException) {
						Timber.w(e, "Failed to reverse geocode: %s", actualLocation)
						ReverseGeocodingResult.Failure.Io(e)
					}
				}
				is LocationResult.Failure ->
					ReverseGeocodingResult.Failure.Location
			}
		}.subscribeOn(Schedulers.io())
}

private sealed class ReverseGeocodingResult {
	data class Success(val name: String?) : ReverseGeocodingResult()
	sealed class Failure : ReverseGeocodingResult() {
		object Location : Failure()
		data class Io(val exception: IOException) : Failure()
	}
}

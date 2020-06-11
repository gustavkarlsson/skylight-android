package se.gustavkarlsson.skylight.android.lib.reversegeocoder

import android.location.Address
import android.location.Geocoder
import com.jakewharton.rx.replayingShare
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import org.threeten.bp.Duration
import se.gustavkarlsson.skylight.android.entities.Loadable
import se.gustavkarlsson.skylight.android.entities.Location
import se.gustavkarlsson.skylight.android.lib.location.LocationResult
import se.gustavkarlsson.skylight.android.utils.delay
import timber.log.Timber
import java.io.IOException

internal class AndroidReverseGeocoder(
    private val geocoder: Geocoder,
    private val retryDelay: Duration
) : ReverseGeocoder {

    override fun get(location: Single<LocationResult>): Single<ReverseGeocodingResult> =
        location
            .flatMap(::getSingleName)
            .doOnSuccess { Timber.i("Provided location name: %s", it) }

    override fun stream(
        locations: Observable<Loadable<LocationResult>>
    ): Observable<Loadable<ReverseGeocodingResult>> =
        locations
            .switchMap { loadableLocationResult ->
                when (loadableLocationResult) {
                    Loadable.Loading -> Observable.just(Loadable.Loading)
                    is Loadable.Loaded -> getSingleNameWithRetry(loadableLocationResult.value)
                }
            }
            .distinctUntilChanged()
            .doOnNext { Timber.i("Streamed location name: %s", it) }
            .replayingShare(Loadable.Loading)

    private fun getSingleNameWithRetry(
        locationResult: LocationResult
    ): Observable<Loadable.Loaded<ReverseGeocodingResult>> =
        getSingleName(locationResult)
            .flatMapObservable { reverseGeocodingResult ->
                when (reverseGeocodingResult) {
                    is ReverseGeocodingResult.Failure.Io ->
                        Observable.concat(
                            Observable.just(Loadable.Loaded(reverseGeocodingResult)),
                            Observable.error(reverseGeocodingResult.exception)
                        )
                    else -> Observable.just(Loadable.Loaded(reverseGeocodingResult))
                }
            }
            .retryWhen { it.delay(retryDelay) }

    private fun getSingleName(locationResult: LocationResult): Single<ReverseGeocodingResult> =
        Single.defer {
            when (locationResult) {
                is LocationResult.Success -> getSingleName(locationResult.location)
                is LocationResult.Failure -> Single.just(ReverseGeocodingResult.Failure.Location)
            }
        }

    private fun getSingleName(location: Location): Single<ReverseGeocodingResult> =
        Single.fromCallable {
            try {
                val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 10)
                val bestName = addresses.getBestName()
                if (bestName == null) ReverseGeocodingResult.Failure.NotFound
                else ReverseGeocodingResult.Success(bestName)
            } catch (e: IOException) {
                Timber.w(e, "Failed to reverse geocode: %s", location)
                ReverseGeocodingResult.Failure.Io(e)
            }
        }.subscribeOn(Schedulers.io())
}

private fun List<Address>.getBestName() =
    mapNotNull { it.locality ?: it.subAdminArea ?: it.adminArea }.firstOrNull()

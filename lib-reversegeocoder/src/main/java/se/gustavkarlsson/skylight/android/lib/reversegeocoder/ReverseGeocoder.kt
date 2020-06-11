package se.gustavkarlsson.skylight.android.lib.reversegeocoder

import io.reactivex.Observable
import io.reactivex.Single
import se.gustavkarlsson.skylight.android.entities.Loadable
import se.gustavkarlsson.skylight.android.entities.LocationResult

interface ReverseGeocoder {
    fun get(location: Single<LocationResult>): Single<ReverseGeocodingResult>
    fun stream(
        locations: Observable<Loadable<LocationResult>>
    ): Observable<Loadable<ReverseGeocodingResult>>
}

package se.gustavkarlsson.skylight.android.lib.geomaglocation

import io.reactivex.Observable
import io.reactivex.Single
import se.gustavkarlsson.skylight.android.entities.Loadable
import se.gustavkarlsson.skylight.android.entities.Report
import se.gustavkarlsson.skylight.android.lib.location.LocationResult

interface GeomagLocationProvider {
    fun get(location: Single<LocationResult>): Single<Report<GeomagLocation>>
    fun stream(
        locations: Observable<Loadable<LocationResult>>
    ): Observable<Loadable<Report<GeomagLocation>>>
}

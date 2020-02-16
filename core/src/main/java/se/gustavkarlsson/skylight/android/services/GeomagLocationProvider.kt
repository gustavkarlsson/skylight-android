package se.gustavkarlsson.skylight.android.services

import io.reactivex.Observable
import io.reactivex.Single
import se.gustavkarlsson.skylight.android.entities.GeomagLocation
import se.gustavkarlsson.skylight.android.entities.Loadable
import se.gustavkarlsson.skylight.android.entities.LocationResult
import se.gustavkarlsson.skylight.android.entities.Report

interface GeomagLocationProvider {
	fun get(location: Single<LocationResult>): Single<Report<GeomagLocation>>
	fun stream(locations: Observable<Loadable<LocationResult>>): Observable<Loadable<Report<GeomagLocation>>>
}

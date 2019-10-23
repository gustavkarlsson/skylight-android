package se.gustavkarlsson.skylight.android.services

import io.reactivex.Flowable
import io.reactivex.Single
import se.gustavkarlsson.skylight.android.entities.GeomagLocation
import se.gustavkarlsson.skylight.android.entities.Loadable
import se.gustavkarlsson.skylight.android.entities.LocationResult
import se.gustavkarlsson.skylight.android.entities.Report

interface GeomagLocationProvider {
	fun get(location: Single<LocationResult>): Single<Report<GeomagLocation>>
	fun stream(locations: Flowable<Loadable<LocationResult>>): Flowable<Loadable<Report<GeomagLocation>>>
}

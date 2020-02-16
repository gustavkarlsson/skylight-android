package se.gustavkarlsson.skylight.android.services

import io.reactivex.Observable
import io.reactivex.Single
import se.gustavkarlsson.skylight.android.entities.Loadable
import se.gustavkarlsson.skylight.android.entities.LocationResult
import se.gustavkarlsson.skylight.android.entities.Report
import se.gustavkarlsson.skylight.android.entities.Weather

interface WeatherProvider {
	fun get(location: Single<LocationResult>): Single<Report<Weather>>
	fun stream(locations: Observable<Loadable<LocationResult>>): Observable<Loadable<Report<Weather>>>
}

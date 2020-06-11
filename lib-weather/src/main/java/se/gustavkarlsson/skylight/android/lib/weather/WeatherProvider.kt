package se.gustavkarlsson.skylight.android.lib.weather

import io.reactivex.Observable
import io.reactivex.Single
import se.gustavkarlsson.skylight.android.entities.Loadable
import se.gustavkarlsson.skylight.android.entities.LocationResult
import se.gustavkarlsson.skylight.android.entities.Report

interface WeatherProvider {
    fun get(location: Single<LocationResult>): Single<Report<Weather>>
    fun stream(
        locations: Observable<Loadable<LocationResult>>
    ): Observable<Loadable<Report<Weather>>>
}

package se.gustavkarlsson.skylight.android.lib.weather

import io.reactivex.Observable
import io.reactivex.Single
import se.gustavkarlsson.skylight.android.core.entities.Loadable
import se.gustavkarlsson.skylight.android.core.entities.Report
import se.gustavkarlsson.skylight.android.lib.location.LocationResult

interface WeatherProvider {
    fun get(location: Single<LocationResult>): Single<Report<Weather>>
    fun stream(
        locations: Observable<Loadable<LocationResult>>
    ): Observable<Loadable<Report<Weather>>>
}

package se.gustavkarlsson.skylight.android.lib.darkness

import io.reactivex.Observable
import io.reactivex.Single
import se.gustavkarlsson.skylight.android.entities.Loadable
import se.gustavkarlsson.skylight.android.entities.Report
import se.gustavkarlsson.skylight.android.lib.location.LocationResult

interface DarknessProvider {
    fun get(location: Single<LocationResult>): Single<Report<Darkness>>
    fun stream(
        locations: Observable<Loadable<LocationResult>>
    ): Observable<Loadable<Report<Darkness>>>
}

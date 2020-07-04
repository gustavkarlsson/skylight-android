package se.gustavkarlsson.skylight.android.lib.aurora

import io.reactivex.Observable
import io.reactivex.Single
import se.gustavkarlsson.skylight.android.entities.Loadable
import se.gustavkarlsson.skylight.android.lib.location.LocationResult

interface AuroraReportProvider {
    fun get(location: Single<LocationResult>): Single<CompleteAuroraReport>
    fun stream(locations: Observable<Loadable<LocationResult>>): Observable<LoadableAuroraReport>
}

package se.gustavkarlsson.skylight.android.services

import io.reactivex.Observable
import io.reactivex.Single
import se.gustavkarlsson.skylight.android.entities.Loadable
import se.gustavkarlsson.skylight.android.entities.LocationResult

interface LocationProvider {
    fun get(): Single<LocationResult>
    fun stream(): Observable<Loadable<LocationResult>>
}

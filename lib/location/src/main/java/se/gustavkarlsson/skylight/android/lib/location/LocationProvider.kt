package se.gustavkarlsson.skylight.android.lib.location

import io.reactivex.Observable
import io.reactivex.Single
import se.gustavkarlsson.skylight.android.core.entities.Loadable

interface LocationProvider {
    fun get(): Single<LocationResult>
    fun stream(): Observable<Loadable<LocationResult>>
}

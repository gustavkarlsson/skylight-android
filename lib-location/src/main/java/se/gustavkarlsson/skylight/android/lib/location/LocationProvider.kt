package se.gustavkarlsson.skylight.android.lib.location

import io.reactivex.Flowable
import io.reactivex.Single
import se.gustavkarlsson.skylight.android.entities.Loadable
import se.gustavkarlsson.skylight.android.entities.LocationResult

interface LocationProvider {
	fun get(): Single<LocationResult>
	fun stream(): Flowable<Loadable<LocationResult>>
}

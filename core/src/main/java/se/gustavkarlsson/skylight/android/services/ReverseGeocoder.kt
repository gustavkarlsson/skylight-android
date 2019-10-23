package se.gustavkarlsson.skylight.android.services

import io.reactivex.Flowable
import io.reactivex.Single
import se.gustavkarlsson.koptional.Optional
import se.gustavkarlsson.skylight.android.entities.Loadable
import se.gustavkarlsson.skylight.android.entities.LocationResult

interface ReverseGeocoder {
	fun get(location: Single<LocationResult>): Single<Optional<String>>
	fun stream(locations: Flowable<Loadable<LocationResult>>): Flowable<Loadable<String?>>
}

package se.gustavkarlsson.skylight.android.services

import io.reactivex.Flowable
import io.reactivex.Single
import se.gustavkarlsson.koptional.Optional
import se.gustavkarlsson.skylight.android.entities.Location

interface ReverseGeocoder {
	fun get(location: Single<Optional<Location>>): Single<Optional<String>>
	fun stream(locations: Flowable<Optional<Location>>): Flowable<Optional<String>>
}

package se.gustavkarlsson.skylight.android.services.providers

import io.reactivex.Flowable
import io.reactivex.Single
import se.gustavkarlsson.koptional.Optional
import se.gustavkarlsson.skylight.android.entities.Location

interface LocationProvider {
	fun get(): Single<Optional<Location>>
	val stream: Flowable<Optional<Location>>
}

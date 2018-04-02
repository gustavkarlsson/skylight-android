package se.gustavkarlsson.skylight.android.services.providers

import io.reactivex.Single
import se.gustavkarlsson.skylight.android.entities.Location

interface LocationProvider {
	fun get(): Single<Location>
}

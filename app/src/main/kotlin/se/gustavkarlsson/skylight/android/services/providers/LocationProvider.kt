package se.gustavkarlsson.skylight.android.services.providers

import io.reactivex.Single
import se.gustavkarlsson.skylight.android.services.Location


interface LocationProvider {
	fun getLocation(): Single<Location>
}

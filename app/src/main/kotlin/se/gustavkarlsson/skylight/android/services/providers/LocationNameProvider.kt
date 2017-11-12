package se.gustavkarlsson.skylight.android.services.providers

import io.reactivex.Maybe
import io.reactivex.Single
import se.gustavkarlsson.skylight.android.services.Location

interface LocationNameProvider {
	fun getLocationName(location: Single<Location>): Maybe<String>
}

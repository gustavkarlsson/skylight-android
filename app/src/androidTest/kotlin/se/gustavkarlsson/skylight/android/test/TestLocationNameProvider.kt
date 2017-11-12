package se.gustavkarlsson.skylight.android.test

import io.reactivex.Maybe
import io.reactivex.Single
import se.gustavkarlsson.skylight.android.services.Location
import se.gustavkarlsson.skylight.android.services.providers.LocationNameProvider

class TestLocationNameProvider(
	var delegate: () -> String?
) : LocationNameProvider {
	override fun getLocationName(location: Single<Location>): Maybe<String> {
		return Maybe.fromCallable(delegate)
	}
}

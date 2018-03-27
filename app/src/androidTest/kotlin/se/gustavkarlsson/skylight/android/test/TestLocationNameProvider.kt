package se.gustavkarlsson.skylight.android.test

import io.reactivex.Maybe
import se.gustavkarlsson.skylight.android.entities.Location
import se.gustavkarlsson.skylight.android.services.providers.LocationNameProvider

class TestLocationNameProvider(
	var delegate: () -> String?
) : LocationNameProvider {
	override fun getLocationName(location: Location): Maybe<String> {
		return Maybe.fromCallable(delegate)
	}
}

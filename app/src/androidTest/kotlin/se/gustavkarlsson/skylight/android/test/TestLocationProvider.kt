package se.gustavkarlsson.skylight.android.test

import io.reactivex.Single
import se.gustavkarlsson.skylight.android.services.Location
import se.gustavkarlsson.skylight.android.services.providers.LocationProvider

class TestLocationProvider(
	var delegate: () -> Location
) : LocationProvider {

	override fun getLocation(): Single<Location> {
		return Single.fromCallable(delegate)
	}
}

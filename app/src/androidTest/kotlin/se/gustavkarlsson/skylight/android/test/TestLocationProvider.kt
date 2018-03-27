package se.gustavkarlsson.skylight.android.test

import io.reactivex.Single
import se.gustavkarlsson.skylight.android.entities.Location
import se.gustavkarlsson.skylight.android.services.providers.LocationProvider

class TestLocationProvider(
	var delegate: () -> Location
) : LocationProvider {

	override fun get(): Single<Location> {
		return Single.fromCallable(delegate)
	}
}

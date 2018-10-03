package se.gustavkarlsson.skylight.android.test

import io.reactivex.Single
import se.gustavkarlsson.koptional.Optional
import se.gustavkarlsson.skylight.android.entities.Location
import se.gustavkarlsson.skylight.android.services.providers.LocationNameProvider

class TestLocationNameProvider(
	var delegate: () -> Optional<String>
) : LocationNameProvider {
	override fun get(location: Single<Optional<Location>>): Single<Optional<String>> {
		return Single.fromCallable { delegate() }
	}
}

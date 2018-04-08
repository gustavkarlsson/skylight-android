package se.gustavkarlsson.skylight.android.test

import com.hadisatrio.optional.Optional
import io.reactivex.Single
import se.gustavkarlsson.skylight.android.entities.Location
import se.gustavkarlsson.skylight.android.services.providers.LocationProvider

class TestLocationProvider(
	var delegate: () -> Optional<Location>
) : LocationProvider {

	override fun get(): Single<Optional<Location>> {
		return Single.fromCallable({ delegate() })
	}
}

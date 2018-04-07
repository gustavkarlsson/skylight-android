package se.gustavkarlsson.skylight.android.test

import com.hadisatrio.optional.Optional
import io.reactivex.Single
import se.gustavkarlsson.skylight.android.entities.Location
import se.gustavkarlsson.skylight.android.services.providers.LocationNameProvider

class TestLocationNameProvider(
	var delegate: () -> String?
) : LocationNameProvider {
	override fun get(location: Single<Optional<Location>>): Single<Optional<String>> {
		return Single.fromCallable({ Optional.of<String>(delegate()) })
	}
}

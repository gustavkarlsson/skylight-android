package se.gustavkarlsson.skylight.android.services.providers

import com.hadisatrio.optional.Optional
import io.reactivex.Single
import se.gustavkarlsson.skylight.android.entities.Location

interface LocationNameProvider {
	fun get(location: Single<Optional<Location>>): Single<Optional<String>>
}

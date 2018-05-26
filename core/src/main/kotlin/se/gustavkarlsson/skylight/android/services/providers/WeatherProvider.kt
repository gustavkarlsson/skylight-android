package se.gustavkarlsson.skylight.android.services.providers

import com.hadisatrio.optional.Optional
import io.reactivex.Single
import se.gustavkarlsson.skylight.android.entities.Location
import se.gustavkarlsson.skylight.android.entities.Weather

interface WeatherProvider {
	fun get(location: Single<Optional<Location>>): Single<Weather>
}

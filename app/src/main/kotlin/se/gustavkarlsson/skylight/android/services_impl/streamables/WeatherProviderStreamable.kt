package se.gustavkarlsson.skylight.android.services_impl.streamables

import com.hadisatrio.optional.Optional
import io.reactivex.Flowable
import io.reactivex.Single
import se.gustavkarlsson.skylight.android.entities.Location
import se.gustavkarlsson.skylight.android.entities.Weather
import se.gustavkarlsson.skylight.android.extensions.delay
import se.gustavkarlsson.skylight.android.extensions.minutes
import se.gustavkarlsson.skylight.android.extensions.seconds
import se.gustavkarlsson.skylight.android.services.Streamable
import se.gustavkarlsson.skylight.android.services.providers.WeatherProvider
import timber.log.Timber

class WeatherProviderStreamable(
	locations: Flowable<Location>,
	weatherProvider: WeatherProvider
) : Streamable<Weather> {
	override val stream: Flowable<Weather> = locations
		.switchMap {
			weatherProvider.get(Single.just(Optional.of(it)))
				.repeatWhen { it.delay(POLLING_INTERVAL) }
				.retryWhen { it.delay(RETRY_DELAY) }
		}
		.doOnNext { Timber.i("Streamed weather: %s", it) }

	companion object {
		val POLLING_INTERVAL = 15.minutes
		val RETRY_DELAY = 10.seconds
	}
}

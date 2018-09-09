package se.gustavkarlsson.skylight.android.services_impl.streamables

import com.hadisatrio.optional.Optional
import io.reactivex.Flowable
import io.reactivex.Single
import org.threeten.bp.Duration
import se.gustavkarlsson.skylight.android.entities.Location
import se.gustavkarlsson.skylight.android.entities.Weather
import se.gustavkarlsson.skylight.android.extensions.delay
import se.gustavkarlsson.skylight.android.services.Streamable
import se.gustavkarlsson.skylight.android.services.providers.WeatherProvider
import timber.log.Timber

class WeatherProviderStreamable(
	locations: Flowable<Location>,
	weatherProvider: WeatherProvider,
	pollingInterval: Duration,
	retryDelay: Duration
) : Streamable<Weather> {
	override val stream: Flowable<Weather> = locations
		.switchMap {
			weatherProvider.get(Single.just(Optional.of(it)))
				.repeatWhen { it.delay(pollingInterval) }
				.retryWhen { it.delay(retryDelay) }
		}
		.doOnNext { Timber.i("Streamed weather: %s", it) }
}

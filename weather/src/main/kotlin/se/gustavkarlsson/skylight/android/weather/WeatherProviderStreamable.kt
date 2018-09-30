package se.gustavkarlsson.skylight.android.weather

import com.hadisatrio.optional.Optional
import io.reactivex.Flowable
import io.reactivex.Single
import org.threeten.bp.Duration
import se.gustavkarlsson.skylight.android.entities.Location
import se.gustavkarlsson.skylight.android.entities.Report
import se.gustavkarlsson.skylight.android.entities.Weather
import se.gustavkarlsson.skylight.android.extensions.delay
import se.gustavkarlsson.skylight.android.services.Streamable
import se.gustavkarlsson.skylight.android.services.providers.WeatherProvider
import timber.log.Timber

internal class WeatherProviderStreamable(
	locations: Flowable<Location>,
	weatherProvider: WeatherProvider,
	pollingInterval: Duration
) : Streamable<Report<Weather>> {
	override val stream: Flowable<Report<Weather>> = locations
		.switchMap { location ->
			val maybeLocation = Single.just(Optional.of(location))
			weatherProvider.get(maybeLocation)
				.repeatWhen { it.delay(pollingInterval) }
		}
		.distinctUntilChanged()
		.doOnNext { Timber.i("Streamed weather: %s", it) }
}

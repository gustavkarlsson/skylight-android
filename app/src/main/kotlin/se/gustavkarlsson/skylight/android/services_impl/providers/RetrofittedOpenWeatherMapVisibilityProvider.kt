package se.gustavkarlsson.skylight.android.services_impl.providers

import com.hadisatrio.optional.Optional
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import se.gustavkarlsson.skylight.android.entities.Location
import se.gustavkarlsson.skylight.android.entities.Visibility
import se.gustavkarlsson.skylight.android.services.providers.VisibilityProvider
import se.gustavkarlsson.skylight.android.services_impl.providers.openweathermap.OpenWeatherMapApi
import timber.log.Timber

class RetrofittedOpenWeatherMapVisibilityProvider constructor(
	private val api: OpenWeatherMapApi,
	private val appId: String
) : VisibilityProvider {

	override fun get(location: Single<Optional<Location>>): Single<Visibility> {
		return location
			.flatMap {
				it.orNull()?.let {
					api.get(it.latitude, it.longitude, "json", appId)
						.subscribeOn(Schedulers.io())
						.map { Visibility(it.clouds.percentage) }
						.doOnError { Timber.w(it, "Failed to get Visibility from OpenWeatherMap API") }
						.onErrorReturnItem(Visibility())
				} ?: Single.just(Visibility())
			}
			.doOnSuccess { Timber.i("Provided visibility: %s", it) }
	}
}

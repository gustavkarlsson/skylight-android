package se.gustavkarlsson.skylight.android.services_impl.providers.openweathermap

import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.AnkoLogger
import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.entities.Location
import se.gustavkarlsson.skylight.android.entities.Visibility
import se.gustavkarlsson.skylight.android.services.providers.VisibilityProvider
import se.gustavkarlsson.skylight.android.util.UserFriendlyException

class RetrofittedOpenWeatherMapVisibilityProvider constructor(
	private val api: OpenWeatherMapApi,
	private val appId: String
) : VisibilityProvider, AnkoLogger {

	override fun get(location: Single<Location>): Single<Visibility> {
		return location
			.flatMap {
				api.get(it.latitude, it.longitude, "json", appId)
					.subscribeOn(Schedulers.io())
			}
			.onErrorResumeNext {
				Single.error(
					UserFriendlyException(
						R.string.error_could_not_determine_visibility,
						it
					)
				)
			}
			.map {
				Visibility(it.clouds.percentage)
			}
	}
}

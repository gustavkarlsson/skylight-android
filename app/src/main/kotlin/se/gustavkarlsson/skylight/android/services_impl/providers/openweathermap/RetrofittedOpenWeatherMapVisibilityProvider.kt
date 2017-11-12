package se.gustavkarlsson.skylight.android.services_impl.providers.openweathermap

import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.AnkoLogger
import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.entities.Visibility
import se.gustavkarlsson.skylight.android.services.Location
import se.gustavkarlsson.skylight.android.services.providers.VisibilityProvider
import se.gustavkarlsson.skylight.android.util.UserFriendlyException

class RetrofittedOpenWeatherMapVisibilityProvider constructor(
	private val service: OpenWeatherMapService,
	private val appId: String
) : VisibilityProvider, AnkoLogger {

	override fun getVisibility(location: Single<Location>): Single<Visibility> {
		return service.get(location.blockingGet().latitude, location.blockingGet().longitude, "json", appId)
			.subscribeOn(Schedulers.io())
			.onErrorResumeNext {
				Single.error(UserFriendlyException(R.string.error_could_not_determine_visibility, it))
			}.map {
			Visibility(it.clouds.percentage)
		}
	}
}

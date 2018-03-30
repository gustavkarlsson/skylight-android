package se.gustavkarlsson.skylight.android.services_impl.providers

import dagger.Reusable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import pl.charmas.android.reactivelocation2.ReactiveLocationProvider
import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.entities.Location
import se.gustavkarlsson.skylight.android.services.providers.LocationProvider
import se.gustavkarlsson.skylight.android.util.UserFriendlyException
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException
import javax.inject.Inject

@Reusable
class RxLocationProvider
@Inject
constructor(
	private val reactiveLocationProvider: ReactiveLocationProvider
) : LocationProvider {

	override fun get(): Single<Location> {
		return reactiveLocationProvider.lastKnownLocation
			.subscribeOn(Schedulers.io())
			.firstOrError()
			.timeout(timeoutMillis, TimeUnit.MILLISECONDS)
			.map {
				Location(it.latitude, it.longitude)
			}
			.onErrorResumeNext {
				when (it) {
					is TimeoutException -> UserFriendlyException(
						R.string.error_could_not_determine_location,
						"Timed out after $timeoutMillis ms"
					)
					else -> UserFriendlyException(R.string.error_could_not_determine_location, it)
				}.let { Single.error(it) }
			}
	}

	companion object {
		private const val timeoutMillis = 5000L
	}
}

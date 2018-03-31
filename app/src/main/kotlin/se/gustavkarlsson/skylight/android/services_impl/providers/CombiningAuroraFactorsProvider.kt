package se.gustavkarlsson.skylight.android.services_impl.providers

import dagger.Reusable
import io.reactivex.Single
import io.reactivex.functions.Function4
import io.reactivex.schedulers.Schedulers
import org.threeten.bp.Instant
import se.gustavkarlsson.skylight.android.entities.*
import se.gustavkarlsson.skylight.android.services.providers.*
import timber.log.Timber
import javax.inject.Inject

@Reusable
class CombiningAuroraFactorsProvider
@Inject
constructor(
	private val kpIndexProvider: KpIndexProvider,
	private val visibilityProvider: VisibilityProvider,
	private val darknessProvider: DarknessProvider,
	private val geomagLocationProvider: GeomagLocationProvider
) : AuroraFactorsProvider {

	override fun get(time: Single<Instant>, location: Single<Location>): Single<AuroraFactors> {
		return Single.zip(
			kpIndexProvider.get(),
			geomagLocationProvider.get(location),
			darknessProvider.get(time, location),
			visibilityProvider.get(location),
			Function4<KpIndex, GeomagLocation, Darkness, Visibility, AuroraFactors>
			{ kpIndex, geomagLocation, darkness, visibility ->
				AuroraFactors(kpIndex, geomagLocation, darkness, visibility)
			})
			.subscribeOn(Schedulers.computation())
			.doOnSuccess { Timber.i("Provided aurora factors: %s", it) }
	}
}

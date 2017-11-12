package se.gustavkarlsson.skylight.android.services_impl.providers

import dagger.Reusable
import io.reactivex.Single
import io.reactivex.functions.Function4
import org.threeten.bp.Clock
import se.gustavkarlsson.skylight.android.entities.AuroraFactors
import se.gustavkarlsson.skylight.android.extensions.now
import se.gustavkarlsson.skylight.android.services.Location
import se.gustavkarlsson.skylight.android.services.providers.*
import javax.inject.Inject

@Reusable
class AggregatingAuroraFactorsProvider
@Inject
constructor(
	private val kpIndexProvider: KpIndexProvider,
	private val visibilityProvider: VisibilityProvider,
	private val darknessProvider: DarknessProvider,
	private val geomagLocationProvider: GeomagLocationProvider,
	private val clock: Clock // TODO Create CurrentTimeProvider that returns Single<Instant>
) : AuroraFactorsProvider {

	override fun getAuroraFactors(location: Single<Location>): Single<AuroraFactors> {
		val currentTime = Single.fromCallable { clock.now }
		val kpIndexSingle = kpIndexProvider.getKpIndex()
		val geomagLocationSingle = geomagLocationProvider.getGeomagLocation(location)
		val darknessSingle = darknessProvider.getDarkness(currentTime, location)
		val visibilitySingle = visibilityProvider.getVisibility(location)

		return Single.zip(kpIndexSingle, geomagLocationSingle, darknessSingle, visibilitySingle,
			Function4 { kpIndex, geomagLocation, darkness, visibility ->
				AuroraFactors(kpIndex, geomagLocation, darkness, visibility)
			})
	}
}

package se.gustavkarlsson.skylight.android.services_impl.providers

import dagger.Reusable
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.runBlocking
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
	private val clock: Clock
) : AuroraFactorsProvider { // TODO Make sure retrofit calls use coroutines, or replace everything with RX

    override fun getAuroraFactors(location: Location): AuroraFactors {
		return runBlocking {
			val kpIndex = async(CommonPool) { kpIndexProvider.getKpIndex() }
			val geomagLocation = async(CommonPool) { geomagLocationProvider.getGeomagLocation(location.latitude, location.longitude) }
			val darkness = async(CommonPool) { darknessProvider.getDarkness(clock.now, location.latitude, location.longitude) }
			val visibility = async(CommonPool) { visibilityProvider.getVisibility(location.latitude, location.longitude) }
			AuroraFactors(kpIndex.await(), geomagLocation.await(), darkness.await(), visibility.await())
		}
    }
}

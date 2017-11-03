package se.gustavkarlsson.skylight.android.services_impl.providers

import dagger.Reusable
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
) : AuroraFactorsProvider {

    suspend override fun getAuroraFactors(location: Location): AuroraFactors {
        val kpIndex = kpIndexProvider.getKpIndex()
        val geomagLocation = geomagLocationProvider.getGeomagLocation(location.latitude, location.longitude)
        val darkness = darknessProvider.getDarkness(clock.now, location.latitude, location.longitude)
        val visibility = visibilityProvider.getVisibility(location.latitude, location.longitude)
        return AuroraFactors(kpIndex, geomagLocation, darkness, visibility)
    }
}

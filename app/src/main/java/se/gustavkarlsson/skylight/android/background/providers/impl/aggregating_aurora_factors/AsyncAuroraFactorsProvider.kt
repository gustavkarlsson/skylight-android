package se.gustavkarlsson.skylight.android.background.providers.impl.aggregating_aurora_factors

import android.location.Location
import dagger.Reusable
import org.threeten.bp.Clock
import org.threeten.bp.Duration
import se.gustavkarlsson.skylight.android.background.providers.*
import se.gustavkarlsson.skylight.android.models.AuroraFactors
import javax.inject.Inject

@Reusable
class AsyncAuroraFactorsProvider
@Inject
internal constructor(
		private val geomagActivityProvider: GeomagActivityProvider,
		private val visibilityProvider: VisibilityProvider,
		private val darknessProvider: DarknessProvider,
		private val geomagLocationProvider: GeomagLocationProvider,
		private val executorService: ErrorHandlingExecutorService,
		private val clock: Clock) : AuroraFactorsProvider
{

    // TODO handle all errors in Get-classes
    override fun getAuroraFactors(location: Location, timeout: Duration): AuroraFactors {
        val getGeomagActivity = GetGeomagActivity(geomagActivityProvider)
        val getGeomagLocation = GetGeomagLocation(geomagLocationProvider, location)
        val getDarkness = GetDarkness(darknessProvider, location, clock.instant())
        val getVisibility = GetVisibility(visibilityProvider, location)

        val geomagActivityErrorHandlingFuture = executorService.execute(getGeomagActivity, timeout)
        val geomagLocationErrorHandlingFuture = executorService.execute(getGeomagLocation, timeout)
        val darknessErrorHandlingFuture = executorService.execute(getDarkness, timeout)
        val visibilityErrorHandlingFuture = executorService.execute(getVisibility, timeout)

        val geomagActivity = geomagActivityErrorHandlingFuture.get()
        val geomagLocation = geomagLocationErrorHandlingFuture.get()
        val darkness = darknessErrorHandlingFuture.get()
        val visibility = visibilityErrorHandlingFuture.get()
        return AuroraFactors(geomagActivity, geomagLocation, darkness, visibility)
    }
}

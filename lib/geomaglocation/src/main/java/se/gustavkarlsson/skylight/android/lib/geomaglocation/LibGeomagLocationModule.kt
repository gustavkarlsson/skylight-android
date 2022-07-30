package se.gustavkarlsson.skylight.android.lib.geomaglocation

import com.squareup.anvil.annotations.ContributesTo
import dagger.Module
import dagger.Provides
import se.gustavkarlsson.skylight.android.core.AppScopeMarker
import se.gustavkarlsson.skylight.android.core.services.ChanceEvaluator
import se.gustavkarlsson.skylight.android.core.services.Formatter

@Module
@ContributesTo(AppScopeMarker::class)
object LibGeomagLocationModule {

    @Provides
    internal fun geomagLocationFormatter(impl: GeomagLocationFormatter): Formatter<GeomagLocation> = impl

    @Provides
    internal fun geomagLocationEvaluator(): ChanceEvaluator<GeomagLocation> =
        GeomagLocationEvaluator

    @Provides
    internal fun geomagLocationProvider(): GeomagLocationProvider =
        GeomagLocationProviderImpl
}

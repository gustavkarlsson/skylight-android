package se.gustavkarlsson.skylight.android.lib.geomaglocation

import dagger.Module
import dagger.Provides
import se.gustavkarlsson.skylight.android.core.services.ChanceEvaluator
import se.gustavkarlsson.skylight.android.core.services.Formatter

@Module
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

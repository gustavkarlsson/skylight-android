package se.gustavkarlsson.skylight.android.lib.geomaglocation

import dagger.Module
import dagger.Provides
import dagger.Reusable
import se.gustavkarlsson.skylight.android.core.services.ChanceEvaluator
import se.gustavkarlsson.skylight.android.core.services.Formatter

@Module
object LibGeomagLocationModule {

    @Provides
    @Reusable
    internal fun geomagLocationFormatter(impl: GeomagLocationFormatter): Formatter<GeomagLocation> = impl

    @Provides
    @Reusable
    internal fun geomagLocationEvaluator(): ChanceEvaluator<GeomagLocation> =
        GeomagLocationEvaluator

    @Provides
    @Reusable
    internal fun geomagLocationProvider(): GeomagLocationProvider =
        GeomagLocationProviderImpl
}

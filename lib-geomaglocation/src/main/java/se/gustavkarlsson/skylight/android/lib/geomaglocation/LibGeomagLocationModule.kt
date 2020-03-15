package se.gustavkarlsson.skylight.android.lib.geomaglocation

import dagger.Module
import dagger.Provides
import dagger.Reusable
import io.reactivex.Single
import java.util.Locale
import se.gustavkarlsson.skylight.android.entities.GeomagLocation
import se.gustavkarlsson.skylight.android.services.ChanceEvaluator
import se.gustavkarlsson.skylight.android.services.Formatter
import se.gustavkarlsson.skylight.android.services.GeomagLocationProvider
import se.gustavkarlsson.skylight.android.services.Time

@Module
class LibGeomagLocationModule {

    @Provides
    @Reusable
    internal fun geomagLocationFormatter(locale: Single<Locale>): Formatter<GeomagLocation> =
        GeomagLocationFormatter(locale)

    @Provides
    @Reusable
    internal fun geomagLocationEvaluator(): ChanceEvaluator<GeomagLocation> =
        GeomagLocationEvaluator

    @Provides
    @Reusable
    internal fun geomagLocationProvider(time: Time): GeomagLocationProvider =
        GeomagLocationProviderImpl(time)
}

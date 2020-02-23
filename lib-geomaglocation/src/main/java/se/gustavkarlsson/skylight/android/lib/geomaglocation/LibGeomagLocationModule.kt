package se.gustavkarlsson.skylight.android.lib.geomaglocation

import dagger.Module
import dagger.Provides
import dagger.Reusable
import se.gustavkarlsson.skylight.android.services.GeomagLocationProvider
import se.gustavkarlsson.skylight.android.services.Time

@Module
class LibGeomagLocationModule {

    @Provides
    @Reusable
    internal fun geomagLocationProvider(time: Time): GeomagLocationProvider =
        GeomagLocationProviderImpl(time)
}

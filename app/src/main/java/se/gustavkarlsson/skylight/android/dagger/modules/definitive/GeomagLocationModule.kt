package se.gustavkarlsson.skylight.android.dagger.modules.definitive

import dagger.Binds
import dagger.Module
import dagger.Reusable
import se.gustavkarlsson.skylight.android.background.providers.GeomagLocationProvider
import se.gustavkarlsson.skylight.android.background.providers.impl.GeomagLocationProviderImpl

@Module
abstract class GeomagLocationModule {

    @Binds
    @Reusable
    abstract fun bindGeomagLocationProvider(impl: GeomagLocationProviderImpl): GeomagLocationProvider

}

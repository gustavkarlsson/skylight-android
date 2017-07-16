package se.gustavkarlsson.skylight.android.dagger.modules.definitive

import dagger.Binds
import dagger.Module
import dagger.Reusable
import se.gustavkarlsson.skylight.android.services.providers.GeomagLocationProvider
import se.gustavkarlsson.skylight.android.services_impl.providers.GeomagLocationProviderImpl

@Module
abstract class GeomagLocationModule {

    @Binds
    @Reusable
    abstract fun bindGeomagLocationProvider(impl: GeomagLocationProviderImpl): GeomagLocationProvider

}

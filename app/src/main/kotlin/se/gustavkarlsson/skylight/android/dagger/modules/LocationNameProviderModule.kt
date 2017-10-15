package se.gustavkarlsson.skylight.android.dagger.modules

import dagger.Binds
import dagger.Module
import dagger.Reusable
import se.gustavkarlsson.skylight.android.services.providers.LocationNameProvider
import se.gustavkarlsson.skylight.android.services_impl.providers.GeocoderLocationNameProvider

@Module
abstract class LocationNameProviderModule {

    @Binds
    @Reusable
    abstract fun bindLocationNameProvider(impl: GeocoderLocationNameProvider): LocationNameProvider
}

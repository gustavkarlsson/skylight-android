package se.gustavkarlsson.skylight.android.dagger.modules

import dagger.Binds
import dagger.Module
import dagger.Reusable
import se.gustavkarlsson.skylight.android.services.providers.AddressProvider
import se.gustavkarlsson.skylight.android.services_impl.providers.GeocoderAddressProvider

@Module
abstract class AsyncAddressProviderModule {

    @Binds
    @Reusable
    abstract fun bindAsyncAddressProvider(impl: GeocoderAddressProvider): AddressProvider
}

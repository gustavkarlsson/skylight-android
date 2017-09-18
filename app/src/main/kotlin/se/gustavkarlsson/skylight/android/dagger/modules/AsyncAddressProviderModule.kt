package se.gustavkarlsson.skylight.android.dagger.modules

import dagger.Binds
import dagger.Module
import dagger.Reusable
import se.gustavkarlsson.skylight.android.services.providers.AsyncAddressProvider
import se.gustavkarlsson.skylight.android.services_impl.providers.GeocoderAsyncAddressProvider

@Module(includes = arrayOf(
		GeocoderModule::class,
        ThreadPoolModule::class
))
abstract class AsyncAddressProviderModule {

    @Binds
    @Reusable
    abstract fun bindAsyncAddressProvider(impl: GeocoderAsyncAddressProvider): AsyncAddressProvider
}

package se.gustavkarlsson.skylight.android.lib.scopedservice

import dagger.Module
import dagger.Provides

@Module
object LibScopedServiceModule {

    @Provides
    internal fun provideServiceCatalog(registry: ServiceRegistry): ServiceCatalog = registry

    @Provides
    internal fun provideServiceClearer(registry: ServiceRegistry): ServiceClearer = registry
}

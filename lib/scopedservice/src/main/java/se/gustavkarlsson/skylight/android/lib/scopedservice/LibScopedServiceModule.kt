package se.gustavkarlsson.skylight.android.lib.scopedservice

import dagger.Module
import dagger.Provides
import se.gustavkarlsson.skylight.android.core.AppScope

@Module
object LibScopedServiceModule {

    @Provides
    @AppScope
    internal fun provideServiceRegistry(): ServiceRegistry = ServiceRegistry()

    @Provides
    internal fun provideServiceCatalog(registry: ServiceRegistry): ServiceCatalog = registry

    @Provides
    internal fun provideServiceClearer(registry: ServiceRegistry): ServiceClearer = registry
}

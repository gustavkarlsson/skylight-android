package se.gustavkarlsson.skylight.android.lib.scopedservice

import se.gustavkarlsson.skylight.android.lib.scopedservice.internal.DefaultServiceRegistry
import dagger.Module
import dagger.Provides
import se.gustavkarlsson.skylight.android.services.ServiceRegistry
import javax.inject.Singleton

@Module
class LibScopedServiceModule {

    @Provides
    @Singleton
    internal fun provideServiceRegistry(): ServiceRegistry = DefaultServiceRegistry()
}

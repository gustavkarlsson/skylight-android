package se.gustavkarlsson.skylight.android.lib.scopedservice

import dagger.Module
import dagger.Provides
import javax.inject.Singleton
import se.gustavkarlsson.skylight.android.services.ServiceRegistry

@Module
class LibScopedServiceModule {

    @Provides
    @Singleton
    internal fun provideServiceRegistry(): ServiceRegistry =
        DefaultServiceRegistry()
}

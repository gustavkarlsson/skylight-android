package se.gustavkarlsson.skylight.android.lib.scopedservice

import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class LibScopedServiceModule {

    @Provides
    @Singleton
    internal fun provideServiceRegistry(): ServiceRegistry =
        DefaultServiceRegistry()
}

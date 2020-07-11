package se.gustavkarlsson.skylight.android.lib.scopedservice

import dagger.Module
import dagger.Provides
import se.gustavkarlsson.skylight.android.AppScope

@Module
class LibScopedServiceModule {

    @Provides
    @AppScope
    internal fun provideServiceRegistry(): ServiceRegistry =
        DefaultServiceRegistry()
}

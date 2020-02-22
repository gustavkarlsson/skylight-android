package se.gustavkarlsson.skylight.android.lib.scopedservice

import se.gustavkarlsson.skylight.android.lib.scopedservice.internal.DefaultServiceRegistry
import dagger.Module
import dagger.Provides
import org.koin.dsl.module.module
import javax.inject.Singleton

val libScopedServiceModule = module {

    single<ServiceRegistry> { DefaultServiceRegistry() }
}

@Module
class LibScopedServiceModule {

    @Provides
    @Singleton
    internal fun provideServiceRegistry(): ServiceRegistry = DefaultServiceRegistry()
}

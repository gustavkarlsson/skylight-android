package se.gustavkarlsson.skylight.android.lib.scopedservice

import com.squareup.anvil.annotations.ContributesTo
import dagger.Module
import dagger.Provides
import se.gustavkarlsson.skylight.android.core.AppScopeMarker

@Module
@ContributesTo(AppScopeMarker::class)
object LibScopedServiceModule {

    @Provides
    internal fun provideServiceCatalog(registry: ServiceRegistry): ServiceCatalog = registry

    @Provides
    internal fun provideServiceClearer(registry: ServiceRegistry): ServiceClearer = registry
}

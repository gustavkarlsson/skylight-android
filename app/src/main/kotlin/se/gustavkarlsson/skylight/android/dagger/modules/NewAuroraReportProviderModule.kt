package se.gustavkarlsson.skylight.android.dagger.modules

import dagger.Module
import dagger.Provides
import dagger.Reusable
import se.gustavkarlsson.skylight.android.dagger.LAST_NAME
import se.gustavkarlsson.skylight.android.dagger.NEW_NAME
import se.gustavkarlsson.skylight.android.entities.AuroraReport
import se.gustavkarlsson.skylight.android.services.SingletonCache
import se.gustavkarlsson.skylight.android.services.providers.AuroraReportProvider
import javax.inject.Named

@Module
class NewAuroraReportProviderModule {

    @Provides
    @Reusable
    @Named(NEW_NAME)
    fun provideNewAuroraReportProvider(provider: AuroraReportProvider, @Named(LAST_NAME) cache: SingletonCache<AuroraReport>): AuroraReportProvider {
        // TODO Replace with real implementation
        return object : AuroraReportProvider {
            override fun get(): AuroraReport {
                val auroraReport = provider.get()
                cache.value = auroraReport // TODO Move caching somewhere else
                return auroraReport
            }
        }
    }
}

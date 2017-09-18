package se.gustavkarlsson.skylight.android.dagger.modules

import android.net.ConnectivityManager
import dagger.Module
import dagger.Provides
import org.threeten.bp.Clock
import org.threeten.bp.Duration
import se.gustavkarlsson.skylight.android.dagger.LAST_NAME
import se.gustavkarlsson.skylight.android.entities.AuroraReport
import se.gustavkarlsson.skylight.android.services.SingletonCache
import se.gustavkarlsson.skylight.android.services.providers.AsyncAddressProvider
import se.gustavkarlsson.skylight.android.services.providers.AuroraFactorsProvider
import se.gustavkarlsson.skylight.android.services.providers.AuroraReportProvider
import se.gustavkarlsson.skylight.android.services.providers.LocationProvider
import se.gustavkarlsson.skylight.android.services_impl.providers.RealAuroraReportProvider
import javax.inject.Named

@Module(includes = arrayOf(
        AuroraFactorsModule::class,
		SystemServiceModule::class,
		LocationProviderModule::class,
		AsyncAddressProviderModule::class
))
class NewAuroraReportProviderModule {

    @Provides
    fun provideAuroraReportProvider(
            @Named(LAST_NAME) cache: SingletonCache<AuroraReport>,
            connectivityManager: ConnectivityManager,
            locationProvider: LocationProvider,
            auroraFactorsProvider: AuroraFactorsProvider,
            asyncAddressProvider: AsyncAddressProvider,
            clock: Clock
    ): AuroraReportProvider = RealAuroraReportProvider(cache, connectivityManager, locationProvider, auroraFactorsProvider, asyncAddressProvider, clock, Duration.ofSeconds(30))

}

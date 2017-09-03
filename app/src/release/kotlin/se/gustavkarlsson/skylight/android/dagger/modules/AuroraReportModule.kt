package se.gustavkarlsson.skylight.android.dagger.modules

import android.net.ConnectivityManager
import dagger.Module
import dagger.Provides
import org.threeten.bp.Clock
import org.threeten.bp.Duration
import se.gustavkarlsson.skylight.android.services.providers.AsyncAddressProvider
import se.gustavkarlsson.skylight.android.services.providers.AuroraFactorsProvider
import se.gustavkarlsson.skylight.android.services.providers.AuroraReportProvider
import se.gustavkarlsson.skylight.android.services.providers.LocationProvider
import se.gustavkarlsson.skylight.android.services_impl.providers.AuroraReportProviderImpl

@Module(includes = arrayOf(
        AuroraFactorsModule::class,
		SystemServiceModule::class,
		LocationProviderModule::class,
		AsyncAddressProviderModule::class
))
class AuroraReportModule {

    @Provides
    fun provideAuroraReportProvider(
            connectivityManager: ConnectivityManager,
            locationProvider: LocationProvider,
            auroraFactorsProvider: AuroraFactorsProvider,
            asyncAddressProvider: AsyncAddressProvider,
            clock: Clock
    ): AuroraReportProvider = AuroraReportProviderImpl(connectivityManager, locationProvider, auroraFactorsProvider, asyncAddressProvider, clock, Duration.ofSeconds(30)) // TODO Make configurable

}

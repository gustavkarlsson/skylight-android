package se.gustavkarlsson.skylight.android.dagger.modules

import android.content.Context
import android.net.ConnectivityManager
import dagger.Module
import dagger.Provides
import dagger.Reusable
import org.threeten.bp.Clock
import org.threeten.bp.Duration
import se.gustavkarlsson.skylight.android.dagger.LAST_NAME
import se.gustavkarlsson.skylight.android.dagger.NEW_NAME
import se.gustavkarlsson.skylight.android.entities.AuroraReport
import se.gustavkarlsson.skylight.android.services.DebugSettings
import se.gustavkarlsson.skylight.android.services.SingletonCache
import se.gustavkarlsson.skylight.android.services.providers.AsyncAddressProvider
import se.gustavkarlsson.skylight.android.services.providers.AuroraFactorsProvider
import se.gustavkarlsson.skylight.android.services.providers.AuroraReportProvider
import se.gustavkarlsson.skylight.android.services.providers.LocationProvider
import se.gustavkarlsson.skylight.android.services_impl.SharedPreferencesDebugSettings
import se.gustavkarlsson.skylight.android.services_impl.providers.DebugAuroraReportProvider
import se.gustavkarlsson.skylight.android.services_impl.providers.RealAuroraReportProvider
import javax.inject.Named

@Module
class NewAuroraReportProviderModule {

    @Provides
	@Reusable
	@Named(NEW_NAME)
    fun provideNewAuroraReportProvider(
    		@Named(LAST_NAME) cache: SingletonCache<AuroraReport>,
            debugSettings: DebugSettings,
            connectivityManager: ConnectivityManager,
            locationProvider: LocationProvider,
            auroraFactorsProvider: AuroraFactorsProvider,
            asyncAddressProvider: AsyncAddressProvider,
            clock: Clock
    ): AuroraReportProvider {
        val realProvider = RealAuroraReportProvider(cache, connectivityManager, locationProvider, auroraFactorsProvider, asyncAddressProvider, clock, Duration.ofSeconds(30))
        return DebugAuroraReportProvider(cache, realProvider, debugSettings, clock)
    }

    @Provides
    @Reusable
    fun provideDebugSettings(
            context: Context
    ): DebugSettings = SharedPreferencesDebugSettings(context)

}

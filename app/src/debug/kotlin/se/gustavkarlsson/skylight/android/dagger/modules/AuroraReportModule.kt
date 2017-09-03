package se.gustavkarlsson.skylight.android.dagger.modules

import android.content.Context
import android.net.ConnectivityManager
import dagger.Module
import dagger.Provides
import dagger.Reusable
import org.threeten.bp.Clock
import org.threeten.bp.Duration
import se.gustavkarlsson.skylight.android.services.DebugSettings
import se.gustavkarlsson.skylight.android.services.providers.AsyncAddressProvider
import se.gustavkarlsson.skylight.android.services.providers.AuroraFactorsProvider
import se.gustavkarlsson.skylight.android.services.providers.AuroraReportProvider
import se.gustavkarlsson.skylight.android.services.providers.LocationProvider
import se.gustavkarlsson.skylight.android.services_impl.SharedPreferencesDebugSettings
import se.gustavkarlsson.skylight.android.services_impl.providers.AuroraReportProviderImpl
import se.gustavkarlsson.skylight.android.services_impl.providers.DebugAuroraReportProvider

@Module(includes = arrayOf(
        AuroraFactorsModule::class,
		SystemServiceModule::class,
		LocationProviderModule::class,
		AsyncAddressProviderModule::class
))
class AuroraReportModule {

    @Provides
    fun provideAuroraReportProvider(
            debugSettings: DebugSettings,
            connectivityManager: ConnectivityManager,
            locationProvider: LocationProvider,
            auroraFactorsProvider: AuroraFactorsProvider,
            asyncAddressProvider: AsyncAddressProvider,
            clock: Clock
    ): AuroraReportProvider {
        if (debugSettings.isOverrideValues) {
            return DebugAuroraReportProvider(debugSettings, clock)
        } else {
            return AuroraReportProviderImpl(connectivityManager, locationProvider, auroraFactorsProvider, asyncAddressProvider, clock, Duration.ofSeconds(30)) // TODO Make configurable
        }
    }

    @Provides
    @Reusable
    fun provideDebugSettings(
            context: Context
    ): DebugSettings = SharedPreferencesDebugSettings(context)

}

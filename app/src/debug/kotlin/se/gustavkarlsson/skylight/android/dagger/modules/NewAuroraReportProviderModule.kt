package se.gustavkarlsson.skylight.android.dagger.modules

import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import dagger.Module
import dagger.Provides
import dagger.Reusable
import org.threeten.bp.Clock
import org.threeten.bp.Duration
import se.gustavkarlsson.skylight.android.dagger.NEW_NAME
import se.gustavkarlsson.skylight.android.services.DebugSettings
import se.gustavkarlsson.skylight.android.services.providers.AuroraFactorsProvider
import se.gustavkarlsson.skylight.android.services.providers.AuroraReportProvider
import se.gustavkarlsson.skylight.android.services.providers.LocationNameProvider
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
		debugSettings: DebugSettings,
		connectivityManager: ConnectivityManager,
		locationProvider: LocationProvider,
		auroraFactorsProvider: AuroraFactorsProvider,
		locationNameProvider: LocationNameProvider,
		clock: Clock
	): AuroraReportProvider {
		val realProvider = RealAuroraReportProvider(connectivityManager, locationProvider, auroraFactorsProvider, locationNameProvider, clock, Duration.ofSeconds(30))
		return DebugAuroraReportProvider(realProvider, debugSettings, clock)
	}

	@Provides
	@Reusable
	fun provideDebugSettings(
		sharedPreferences: SharedPreferences,
		context: Context
	): DebugSettings = SharedPreferencesDebugSettings(sharedPreferences, context)

}

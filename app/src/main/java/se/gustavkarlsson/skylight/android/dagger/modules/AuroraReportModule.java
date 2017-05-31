package se.gustavkarlsson.skylight.android.dagger.modules;

import android.net.ConnectivityManager;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.Reusable;
import se.gustavkarlsson.skylight.android.BuildConfig;
import se.gustavkarlsson.skylight.android.background.providers.AsyncAddressProvider;
import se.gustavkarlsson.skylight.android.background.providers.AuroraFactorsProvider;
import se.gustavkarlsson.skylight.android.background.providers.AuroraReportProvider;
import se.gustavkarlsson.skylight.android.background.providers.LocationProvider;
import se.gustavkarlsson.skylight.android.background.providers.impl.AuroraReportProviderImpl;
import se.gustavkarlsson.skylight.android.background.providers.impl.DebugAuroraReportProvider;
import se.gustavkarlsson.skylight.android.background.providers.impl.aggregating_aurora_factors.AggregatingAuroraFactorsProvider;
import se.gustavkarlsson.skylight.android.settings.DebugSettings;

@Module
public abstract class AuroraReportModule {

	@Binds
	@Reusable
	abstract AuroraFactorsProvider bindAuroraFactorsProvider(AggregatingAuroraFactorsProvider provider);

	@Provides
	static AuroraReportProvider provideAuroraReportProvider(DebugSettings debugSettings, ConnectivityManager connectivityManager, LocationProvider locationProvider, AuroraFactorsProvider auroraFactorsProvider, AsyncAddressProvider asyncAddressProvider) {
		if (BuildConfig.DEBUG && debugSettings.isOverrideValues()) {
			return new DebugAuroraReportProvider(debugSettings);
		} else {
			return new AuroraReportProviderImpl(connectivityManager, locationProvider, auroraFactorsProvider, asyncAddressProvider);
		}
	}

}

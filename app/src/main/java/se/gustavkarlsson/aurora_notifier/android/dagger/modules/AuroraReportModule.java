package se.gustavkarlsson.aurora_notifier.android.dagger.modules;

import android.net.ConnectivityManager;

import dagger.Module;
import dagger.Provides;
import se.gustavkarlsson.aurora_notifier.android.BuildConfig;
import se.gustavkarlsson.aurora_notifier.android.background.providers.AsyncAddressProvider;
import se.gustavkarlsson.aurora_notifier.android.background.providers.AuroraFactorsProvider;
import se.gustavkarlsson.aurora_notifier.android.background.providers.AuroraReportProvider;
import se.gustavkarlsson.aurora_notifier.android.background.providers.LocationProvider;
import se.gustavkarlsson.aurora_notifier.android.background.providers.impl.AuroraReportProviderImpl;
import se.gustavkarlsson.aurora_notifier.android.background.providers.impl.DebugAuroraReportProvider;
import se.gustavkarlsson.aurora_notifier.android.settings.DebugSettings;

@Module
public abstract class AuroraReportModule {

	@Provides
	static AuroraReportProvider provideAuroraReportProvider(DebugSettings debugSettings, ConnectivityManager connectivityManager, LocationProvider locationProvider, AuroraFactorsProvider auroraFactorsProvider, AsyncAddressProvider asyncAddressProvider) {
		if (BuildConfig.DEBUG && debugSettings.isOverrideValues()) {
			return new DebugAuroraReportProvider(debugSettings);
		} else {
			return new AuroraReportProviderImpl(connectivityManager, locationProvider, auroraFactorsProvider, asyncAddressProvider);
		}
	}

}

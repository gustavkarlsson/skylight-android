package se.gustavkarlsson.aurora_notifier.android.dagger.modules;

import android.net.ConnectivityManager;

import dagger.Module;
import dagger.Provides;
import se.gustavkarlsson.aurora_notifier.android.BuildConfig;
import se.gustavkarlsson.aurora_notifier.android.background.providers.AsyncAddressProvider;
import se.gustavkarlsson.aurora_notifier.android.background.providers.AuroraDataProvider;
import se.gustavkarlsson.aurora_notifier.android.background.providers.AuroraEvaluationProvider;
import se.gustavkarlsson.aurora_notifier.android.background.providers.LocationProvider;
import se.gustavkarlsson.aurora_notifier.android.background.providers.impl.AuroraEvaluationProviderImpl;
import se.gustavkarlsson.aurora_notifier.android.background.providers.impl.DebugAuroraEvaluationProvider;
import se.gustavkarlsson.aurora_notifier.android.settings.DebugSettings;

@Module
public abstract class AuroraEvaluationModule {

	@Provides
	static AuroraEvaluationProvider provideAuroraEvaluationProvider(DebugSettings debugSettings, ConnectivityManager connectivityManager, LocationProvider locationProvider, AuroraDataProvider auroraDataProvider, AsyncAddressProvider asyncAddressProvider) {
		if (BuildConfig.DEBUG && debugSettings.isOverrideValues()) {
			return new DebugAuroraEvaluationProvider(debugSettings);
		} else {
			return new AuroraEvaluationProviderImpl(connectivityManager, locationProvider, auroraDataProvider, asyncAddressProvider);
		}
	}

}

package se.gustavkarlsson.skylight.android.dagger.modules.replaceable;

import android.content.Context;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import se.gustavkarlsson.skylight.android.cache.DualAuroraReportSingletonCache;
import se.gustavkarlsson.skylight.android.cache.SingletonCache;
import se.gustavkarlsson.skylight.android.models.AuroraReport;

import static se.gustavkarlsson.skylight.android.dagger.Names.LATEST_NAME;

@Module
public abstract class LatestAuroraReportCacheModule {
	private static final String LATEST_CACHE_ID = "latest-aurora-report";

	// Published
	@Provides
	@Singleton
	@Named(LATEST_NAME)
	static SingletonCache<AuroraReport> provideLatestAuroraReportCache(Context context) {
		return new DualAuroraReportSingletonCache(context, LATEST_CACHE_ID);
	}

}

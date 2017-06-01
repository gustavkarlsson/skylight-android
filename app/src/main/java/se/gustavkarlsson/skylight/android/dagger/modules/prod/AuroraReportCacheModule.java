package se.gustavkarlsson.skylight.android.dagger.modules.prod;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;
import se.gustavkarlsson.skylight.android.cache.AuroraReportCache;
import se.gustavkarlsson.skylight.android.cache.DualLruAuroraReportCache;

@Module
public abstract class AuroraReportCacheModule {

	// Published
	@Binds
	@Singleton
	abstract AuroraReportCache bindAuroraReportCache(DualLruAuroraReportCache dualLruAuroraReportCache);

}

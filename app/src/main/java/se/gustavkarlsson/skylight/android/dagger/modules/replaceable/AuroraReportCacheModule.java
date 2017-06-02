package se.gustavkarlsson.skylight.android.dagger.modules.replaceable;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;
import se.gustavkarlsson.skylight.android.cache.DualLastReportCache;
import se.gustavkarlsson.skylight.android.cache.LastReportCache;

@Module
public abstract class AuroraReportCacheModule {

	// Published
	@Binds
	@Singleton
	abstract LastReportCache bindLastReportCache(DualLastReportCache impl);

}

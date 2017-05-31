package se.gustavkarlsson.skylight.android.dagger.modules;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;
import se.gustavkarlsson.skylight.android.cache.AuroraReportCache;
import se.gustavkarlsson.skylight.android.cache.DualLruAuroraReportCache;
import se.gustavkarlsson.skylight.android.cache.DualLruReportNotificationCache;
import se.gustavkarlsson.skylight.android.cache.ReportNotificationCache;

@Module
public abstract class CacheModule {

	@Binds
	@Singleton
	abstract AuroraReportCache bindAuroraReportCache(DualLruAuroraReportCache dualLruAuroraReportCache);

	@Binds
	@Singleton
	abstract ReportNotificationCache bindReportNotificationCache(DualLruReportNotificationCache dualLruReportNotificationCache);

}

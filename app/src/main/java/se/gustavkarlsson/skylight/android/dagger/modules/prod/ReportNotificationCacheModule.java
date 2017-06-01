package se.gustavkarlsson.skylight.android.dagger.modules.prod;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;
import se.gustavkarlsson.skylight.android.cache.DualLruReportNotificationCache;
import se.gustavkarlsson.skylight.android.cache.ReportNotificationCache;

@Module
public abstract class ReportNotificationCacheModule {

	// Published
	@Binds
	@Singleton
	abstract ReportNotificationCache bindReportNotificationCache(DualLruReportNotificationCache dualLruReportNotificationCache);

}

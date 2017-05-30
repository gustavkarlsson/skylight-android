package se.gustavkarlsson.aurora_notifier.android.dagger.modules;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;
import se.gustavkarlsson.aurora_notifier.android.cache.AuroraReportCache;
import se.gustavkarlsson.aurora_notifier.android.cache.DualLruAuroraReportCache;
import se.gustavkarlsson.aurora_notifier.android.cache.DualLruReportNotificationCache;
import se.gustavkarlsson.aurora_notifier.android.cache.ReportNotificationCache;

@Module
public abstract class CacheModule {

	@Binds
	@Singleton
	abstract AuroraReportCache bindAuroraReportCache(DualLruAuroraReportCache dualLruAuroraReportCache);

	@Binds
	@Singleton
	abstract ReportNotificationCache bindReportNotificationCache(DualLruReportNotificationCache dualLruReportNotificationCache);

}

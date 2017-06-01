package se.gustavkarlsson.skylight.android.dagger.components;

import android.content.Context;

import java.util.concurrent.ExecutorService;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Component;
import se.gustavkarlsson.skylight.android.background.UpdateScheduler;
import se.gustavkarlsson.skylight.android.cache.AuroraReportCache;
import se.gustavkarlsson.skylight.android.cache.ReportNotificationCache;
import se.gustavkarlsson.skylight.android.dagger.modules.definitive.ContextModule;
import se.gustavkarlsson.skylight.android.dagger.modules.definitive.ExecutorModule;
import se.gustavkarlsson.skylight.android.dagger.modules.prod.AuroraReportCacheModule;
import se.gustavkarlsson.skylight.android.dagger.modules.prod.ReportNotificationCacheModule;

import static se.gustavkarlsson.skylight.android.dagger.modules.definitive.ExecutorModule.CACHED_THREAD_POOL_NAME;

@Component(modules = {
		ContextModule.class,
		ReportNotificationCacheModule.class,
		AuroraReportCacheModule.class,
		ExecutorModule.class
})
@Singleton
@SuppressWarnings("WeakerAccess")
public interface ApplicationComponent {
	Context getContext();
	AuroraReportCache getAuroraReportCache();
	ReportNotificationCache getReportNotificationCache();
	UpdateScheduler getUpdateScheduler();
	@Named(CACHED_THREAD_POOL_NAME)
	ExecutorService getCachedThreadPool();
}

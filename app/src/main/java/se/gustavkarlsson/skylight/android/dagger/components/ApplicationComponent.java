package se.gustavkarlsson.skylight.android.dagger.components;

import android.content.Context;

import java.util.concurrent.ExecutorService;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Component;
import se.gustavkarlsson.skylight.android.background.UpdateScheduler;
import se.gustavkarlsson.skylight.android.cache.AuroraReportCache;
import se.gustavkarlsson.skylight.android.cache.ReportNotificationCache;
import se.gustavkarlsson.skylight.android.dagger.modules.ApplicationModule;
import se.gustavkarlsson.skylight.android.dagger.modules.CacheModule;
import se.gustavkarlsson.skylight.android.dagger.modules.ExecutorModule;

import static se.gustavkarlsson.skylight.android.dagger.modules.ExecutorModule.CACHED_THREAD_POOL;

@Component(modules = {
		ApplicationModule.class,
		CacheModule.class,
		ExecutorModule.class
})
@Singleton
@SuppressWarnings("WeakerAccess")
public interface ApplicationComponent {
	Context getContext();
	AuroraReportCache getAuroraReportCache();
	ReportNotificationCache getReportNotificationCache();
	UpdateScheduler getUpdateScheduler();
	@Named(CACHED_THREAD_POOL) ExecutorService getExecutorService();
}

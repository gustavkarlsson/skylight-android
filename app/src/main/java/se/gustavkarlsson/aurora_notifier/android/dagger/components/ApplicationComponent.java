package se.gustavkarlsson.aurora_notifier.android.dagger.components;

import android.content.Context;

import java.util.concurrent.ExecutorService;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Component;
import se.gustavkarlsson.aurora_notifier.android.background.UpdateScheduler;
import se.gustavkarlsson.aurora_notifier.android.cache.AuroraReportCache;
import se.gustavkarlsson.aurora_notifier.android.dagger.modules.ApplicationModule;
import se.gustavkarlsson.aurora_notifier.android.dagger.modules.CacheModule;
import se.gustavkarlsson.aurora_notifier.android.dagger.modules.ExecutorModule;

import static se.gustavkarlsson.aurora_notifier.android.dagger.modules.ExecutorModule.CACHED_THREAD_POOL;

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
	UpdateScheduler getUpdateScheduler();
	@Named(CACHED_THREAD_POOL) ExecutorService getExecutorService();
}

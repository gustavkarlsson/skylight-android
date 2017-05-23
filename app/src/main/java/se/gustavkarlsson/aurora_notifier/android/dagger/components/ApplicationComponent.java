package se.gustavkarlsson.aurora_notifier.android.dagger.components;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Component;
import se.gustavkarlsson.aurora_notifier.android.background.UpdateScheduler;
import se.gustavkarlsson.aurora_notifier.android.cache.AuroraReportCache;
import se.gustavkarlsson.aurora_notifier.android.dagger.modules.ApplicationModule;
import se.gustavkarlsson.aurora_notifier.android.dagger.modules.CacheModule;

@Component(modules = {
		ApplicationModule.class,
		CacheModule.class
})
@Singleton
@SuppressWarnings("WeakerAccess")
public interface ApplicationComponent {
	Context getContext();
	AuroraReportCache getAuroraReportCache();
	UpdateScheduler getUpdateScheduler();
}

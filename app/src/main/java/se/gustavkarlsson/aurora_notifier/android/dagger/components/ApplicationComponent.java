package se.gustavkarlsson.aurora_notifier.android.dagger.components;

import javax.inject.Singleton;

import dagger.Component;
import se.gustavkarlsson.aurora_notifier.android.background.UpdateScheduler;
import se.gustavkarlsson.aurora_notifier.android.cache.AuroraEvaluationCache;
import se.gustavkarlsson.aurora_notifier.android.dagger.modules.ApplicationModule;
import se.gustavkarlsson.aurora_notifier.android.dagger.modules.CacheModule;
import se.gustavkarlsson.aurora_notifier.android.dagger.modules.SettingsModule;
import se.gustavkarlsson.aurora_notifier.android.dagger.modules.UpdateSchedulerModule;

@Component(modules = {
		ApplicationModule.class,
		UpdateSchedulerModule.class,
		SettingsModule.class,
		CacheModule.class
})
@Singleton
@SuppressWarnings("WeakerAccess")
public interface ApplicationComponent {
	AuroraEvaluationCache getAuroraEvaluationCache();
	UpdateScheduler getUpdateScheduler();
}

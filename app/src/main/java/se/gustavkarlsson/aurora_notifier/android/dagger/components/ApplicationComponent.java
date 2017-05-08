package se.gustavkarlsson.aurora_notifier.android.dagger.components;

import javax.inject.Singleton;

import dagger.Component;
import se.gustavkarlsson.aurora_notifier.android.cache.AuroraEvaluationCache;
import se.gustavkarlsson.aurora_notifier.android.dagger.modules.CacheModule;

@Component(modules = {
		CacheModule.class
})
@Singleton
@SuppressWarnings("WeakerAccess")
public interface ApplicationComponent {
	AuroraEvaluationCache auroraEvaluationCache();
}

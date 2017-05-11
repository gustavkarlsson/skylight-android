package se.gustavkarlsson.aurora_notifier.android.dagger.modules;

import android.app.Application;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import se.gustavkarlsson.aurora_notifier.android.R;
import se.gustavkarlsson.aurora_notifier.android.cache.AuroraEvaluationCache;
import se.gustavkarlsson.aurora_notifier.android.cache.DualLruAuroraEvaluationCache;

@Module
public abstract class CacheModule {

	@Provides
	@Singleton
	static AuroraEvaluationCache provideAuroraEvaluationCache(Application application) {
		int ramSizeBytes = application.getResources().getInteger(R.integer.setting_aurora_evaluation_cache_max_ram_size_bytes);
		int diskSizeBytes = application.getResources().getInteger(R.integer.setting_aurora_evaluation_cache_max_disk_size_bytes);
		return new DualLruAuroraEvaluationCache(application, ramSizeBytes, diskSizeBytes);
	}

}

package se.gustavkarlsson.aurora_notifier.android.dagger.modules;

import android.content.Context;

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
	static AuroraEvaluationCache provideAuroraEvaluationCache(Context context) {
		int ramSizeBytes = context.getResources().getInteger(R.integer.setting_aurora_evaluation_cache_max_ram_size_bytes);
		int diskSizeBytes = context.getResources().getInteger(R.integer.setting_aurora_evaluation_cache_max_disk_size_bytes);
		return new DualLruAuroraEvaluationCache(context, ramSizeBytes, diskSizeBytes);
	}

}

package se.gustavkarlsson.aurora_notifier.android.cache;

import android.content.Context;

import com.vincentbrison.openlibraries.android.dualcache.Builder;
import com.vincentbrison.openlibraries.android.dualcache.DualCache;

import javax.inject.Inject;
import javax.inject.Singleton;

import se.gustavkarlsson.aurora_notifier.android.BuildConfig;
import se.gustavkarlsson.aurora_notifier.android.models.AuroraEvaluation;

@Singleton
public class DualLruAuroraEvaluationCache implements AuroraEvaluationCache {
	private static final String TAG = DualLruAuroraEvaluationCache.class.getSimpleName();

	private static final String CACHE_NAME = TAG;
	private static final int SIZE_OF_EVALUATION = 2000; // Very rough estimation
	private static final String CURRENT_LOCATION_KEY = "currentLocation";

	private final DualCache<AuroraEvaluation> cache;

	@Inject
	public DualLruAuroraEvaluationCache(Context context, int maxRamSizeBytes, int maxDiskSizeBytes) {
		Builder<AuroraEvaluation> builder = new Builder<AuroraEvaluation>(CACHE_NAME, BuildConfig.VERSION_CODE)
				.useReferenceInRam(maxRamSizeBytes, object -> SIZE_OF_EVALUATION)
				.useSerializerInDisk(maxDiskSizeBytes, false, new GsonCacheSerializer<>(AuroraEvaluation.class), context);
		if (BuildConfig.DEBUG) {
			builder.enableLog();
		}
		this.cache = builder.build();
	}

	@Override
	public AuroraEvaluation getCurrentLocation() {
		return cache.get(CURRENT_LOCATION_KEY);
	}

	@Override
	public void setCurrentLocation(AuroraEvaluation evaluation) {
		cache.put(CURRENT_LOCATION_KEY, evaluation);
	}

}

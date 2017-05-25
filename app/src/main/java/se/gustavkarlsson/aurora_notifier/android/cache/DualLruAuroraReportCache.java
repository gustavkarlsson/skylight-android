package se.gustavkarlsson.aurora_notifier.android.cache;

import android.content.Context;

import com.vincentbrison.openlibraries.android.dualcache.Builder;
import com.vincentbrison.openlibraries.android.dualcache.DualCache;

import se.gustavkarlsson.aurora_notifier.android.BuildConfig;
import se.gustavkarlsson.aurora_notifier.android.models.AuroraReport;

public class DualLruAuroraReportCache implements AuroraReportCache {
	private static final String TAG = DualLruAuroraReportCache.class.getSimpleName();

	private static final String CACHE_NAME = TAG;
	private static final int SIZE_OF_REPORT = 2000; // Very rough estimation
	private static final String CURRENT_LOCATION_KEY = "current_location"; // Must match [a-z0-9_-]{1,64}

	private final DualCache<AuroraReport> cache;

	public DualLruAuroraReportCache(Context context, int maxRamSizeBytes, int maxDiskSizeBytes) {
		Builder<AuroraReport> builder = new Builder<AuroraReport>(CACHE_NAME, BuildConfig.VERSION_CODE)
				.useReferenceInRam(maxRamSizeBytes, object -> SIZE_OF_REPORT)
				.useSerializerInDisk(maxDiskSizeBytes, false, new GsonCacheSerializer<>(AuroraReport.class), context);
		if (BuildConfig.DEBUG) {
			builder.enableLog();
		}
		this.cache = builder.build();
	}

	@Override
	public AuroraReport getCurrentLocation() {
		return cache.get(CURRENT_LOCATION_KEY);
	}

	@Override
	public void setCurrentLocation(AuroraReport report) {
		cache.put(CURRENT_LOCATION_KEY, report);
	}

}

package se.gustavkarlsson.skylight.android.cache;

import android.content.Context;

import com.vincentbrison.openlibraries.android.dualcache.Builder;
import com.vincentbrison.openlibraries.android.dualcache.DualCache;

import javax.inject.Inject;
import javax.inject.Singleton;

import se.gustavkarlsson.skylight.android.BuildConfig;
import se.gustavkarlsson.skylight.android.models.AuroraReport;

@Singleton
public class DualLruAuroraReportCache implements AuroraReportCache {
	private static final String TAG = DualLruAuroraReportCache.class.getSimpleName();

	private static final String CACHE_NAME = TAG;
	private static final String CURRENT_LOCATION_KEY = "current_location"; // Must match [a-z0-9_-]{1,64}

	private final DualCache<AuroraReport> cache;

	@Inject
	DualLruAuroraReportCache(Context context) {
		Builder<AuroraReport> builder = new Builder<AuroraReport>(CACHE_NAME, BuildConfig.VERSION_CODE)
				.useReferenceInRam(Integer.MAX_VALUE, object -> 1)
				.useSerializerInDisk(Integer.MAX_VALUE, false, new GsonCacheSerializer<>(AuroraReport.class), context);
		if (BuildConfig.DEBUG) {
			builder.enableLog();
		}
		this.cache = builder.build();
	}

	@Override
	public AuroraReport get() {
		return cache.get(CURRENT_LOCATION_KEY);
	}

	@Override
	public void set(AuroraReport report) {
		cache.put(CURRENT_LOCATION_KEY, report);
	}

}

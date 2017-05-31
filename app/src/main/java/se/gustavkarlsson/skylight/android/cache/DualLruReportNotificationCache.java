package se.gustavkarlsson.skylight.android.cache;

import android.content.Context;

import com.vincentbrison.openlibraries.android.dualcache.Builder;
import com.vincentbrison.openlibraries.android.dualcache.DualCache;

import javax.inject.Inject;

import se.gustavkarlsson.skylight.android.BuildConfig;
import se.gustavkarlsson.skylight.android.models.AuroraReport;

public class DualLruReportNotificationCache implements ReportNotificationCache {
	private static final String TAG = DualLruReportNotificationCache.class.getSimpleName();

	private static final String CACHE_NAME = TAG;
	private static final String LAST_NOTIFIED_KEY = "last_notified"; // Must match [a-z0-9_-]{1,64}

	private final DualCache<AuroraReport> cache;

	@Inject
	DualLruReportNotificationCache(Context context) {
		Builder<AuroraReport> builder = new Builder<AuroraReport>(CACHE_NAME, BuildConfig.VERSION_CODE)
				.useReferenceInRam(Integer.MAX_VALUE, object -> 1)
				.useSerializerInDisk(Integer.MAX_VALUE, false, new GsonCacheSerializer<>(AuroraReport.class), context);
		if (BuildConfig.DEBUG) {
			builder.enableLog();
		}
		this.cache = builder.build();
	}

	@Override
	public AuroraReport getLastNotified() {
		return cache.get(LAST_NOTIFIED_KEY);
	}

	@Override
	public void setLastNotified(AuroraReport report) {
		cache.put(LAST_NOTIFIED_KEY, report);
	}
}

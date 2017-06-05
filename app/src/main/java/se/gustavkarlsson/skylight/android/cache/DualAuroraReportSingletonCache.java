package se.gustavkarlsson.skylight.android.cache;

import android.content.Context;

import com.vincentbrison.openlibraries.android.dualcache.Builder;
import com.vincentbrison.openlibraries.android.dualcache.DualCache;

import se.gustavkarlsson.skylight.android.BuildConfig;
import se.gustavkarlsson.skylight.android.models.AuroraReport;

public class DualAuroraReportSingletonCache implements SingletonCache<AuroraReport> {
	private static final String KEY = "singleton"; // Must match [a-z0-9_-]{1,64}

	private final DualCache<AuroraReport> dualCache;

	public DualAuroraReportSingletonCache(Context context, String cacheName) {
		Builder<AuroraReport> builder = new Builder<AuroraReport>(cacheName, BuildConfig.VERSION_CODE)
				.useReferenceInRam(Integer.MAX_VALUE, object -> 1)
				.useSerializerInDisk(Integer.MAX_VALUE, false, new GsonCacheSerializer<>(AuroraReport.class), context);
		if (BuildConfig.DEBUG) {
			builder.enableLog();
		}
		this.dualCache = builder.build();
	}

	@Override
	public AuroraReport getValue() {
		return dualCache.get(KEY);
	}

	@Override
	public void setValue(AuroraReport report) {
		dualCache.put(KEY, report);
	}

}

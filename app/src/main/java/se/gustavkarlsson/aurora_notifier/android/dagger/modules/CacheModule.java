package se.gustavkarlsson.aurora_notifier.android.dagger.modules;

import android.content.Context;
import android.os.Parcelable;

import java.io.IOException;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import se.gustavkarlsson.aurora_notifier.android.caching.Cache;
import se.gustavkarlsson.aurora_notifier.android.caching.ParcelCache;

@Module
public class CacheModule {
	public static final String NAME_CACHE_MAX_AGE_MILLIS = "cacheMaxAgeMillis";

	private final Context context;

	public CacheModule(Context context) {
		this.context = context;
	}

	@Provides
	Cache<Parcelable> provideParcelableCache() {
		try {
			return ParcelCache.open(context, "cache", 10_000_000l);
		} catch (IOException e) {
			// Todo log error
			e.printStackTrace();
			return new Cache<Parcelable>() {
				@Override
				public Parcelable get(String key) {
					return null;
				}

				@Override
				public void set(String key, Parcelable value) {

				}

				@Override
				public boolean remove(String key) {
					return false;
				}

				@Override
				public void clear() {

				}

				@Override
				public boolean exists(String key) {
					return false;
				}

				@Override
				public void close() throws IOException {

				}
			};
		}
	}

	@Provides
	@Named(NAME_CACHE_MAX_AGE_MILLIS)
	long provideCacheMaxAgeMillis() {
		return 5_60_000;
	}

}

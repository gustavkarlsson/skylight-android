package se.gustavkarlsson.aurora_notifier.android.dagger.modules;

import android.content.Context;
import android.os.Parcelable;
import android.util.Log;

import java.io.IOException;

import dagger.Module;
import dagger.Provides;
import se.gustavkarlsson.aurora_notifier.android.R;
import se.gustavkarlsson.aurora_notifier.android.caching.Cache;
import se.gustavkarlsson.aurora_notifier.android.caching.ParcelCache;

@Module
public class CacheModule {
	private static final String TAG = CacheModule.class.getSimpleName();

	private final Context context;

	public CacheModule(Context context) {
		this.context = context;
	}

	@Provides
	Cache<Parcelable> provideParcelableCache() {
		try {
			return ParcelCache.open(context, "cache", R.integer.cache_size_bytes);
		} catch (IOException e) {
			Log.e(TAG, "Filed to open ParcelCache. Falling back to NullCache", e);
			return new NullCache<>();
		}
	}

	private static class NullCache<T> implements Cache<T> {
		@Override
		public T get(String key) {
			return null;
		}

		@Override
		public void set(String key, T value) {
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
	}

}

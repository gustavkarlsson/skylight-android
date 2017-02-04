package se.gustavkarlsson.aurora_notifier.android.dagger.modules;

import android.content.Context;
import android.os.Parcelable;
import android.util.Log;

import java.io.IOException;

import dagger.Module;
import dagger.Provides;
import se.gustavkarlsson.aurora_notifier.android.caching.ParcelPersistentCache;
import se.gustavkarlsson.aurora_notifier.android.caching.PersistentCache;

@Module
public class PersistentCacheModule {
	private static final String TAG = PersistentCacheModule.class.getSimpleName();

	private final Context context;
	private final int cacheSizeBytes;

	public PersistentCacheModule(Context context, int cacheSizeBytes) {
		this.context = context;
		this.cacheSizeBytes = cacheSizeBytes;
	}

	@Provides
	PersistentCache<Parcelable> provideParcelableCache() {
		try {
			return ParcelPersistentCache.open(context, "cache", cacheSizeBytes);
		} catch (IOException e) {
			Log.e(TAG, "Failed to open ParcelPersistentCache. Falling back to NullPersistentCache", e);
			return new NullPersistentCache<>();
		}
	}

	private static class NullPersistentCache<T> implements PersistentCache<T> {
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

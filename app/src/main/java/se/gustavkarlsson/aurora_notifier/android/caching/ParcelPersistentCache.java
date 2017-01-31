package se.gustavkarlsson.aurora_notifier.android.caching;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.BadParcelableException;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.jakewharton.disklrucache.DiskLruCache;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ParcelPersistentCache<T extends Parcelable> implements PersistentCache<T> {
	private static final String TAG = ParcelPersistentCache.class.getSimpleName();

	private static final int MAX_KEY_SYMBOLS = 62;

	private final ClassLoader classLoader;
	private final DiskLruCache cache;

	private ParcelPersistentCache(Context context, String name, long maxSizeBytes) throws IOException {
		File rootCacheDir = determineCacheDir(context);
		this.classLoader = context.getClassLoader();
		File cacheDir = new File(rootCacheDir, name);
		int version = getVersionCode(context) + Build.VERSION.SDK_INT;
		this.cache = DiskLruCache.open(cacheDir, version, 1, maxSizeBytes);
	}

	private static File determineCacheDir(Context context) {
		File cacheDir = context.getExternalCacheDir();
		if (cacheDir == null) {
			cacheDir = context.getCacheDir();
		}
		return cacheDir;
	}

	private static int getVersionCode(Context context) {
		int result = 0;
		try {
			PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			result = pInfo.versionCode;
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
		return result;
	}

	public static <T extends Parcelable> ParcelPersistentCache<T> open(Context context, String name, long maxSize) throws IOException {
		return new ParcelPersistentCache<>(context, name, maxSize);
	}

	@Override
	public T get(String key) {
		String normalizedKey = normalizeKey(key);
		Parcel parcel = getParcel(normalizedKey);
		if (parcel == null) {
			return null;
		}
		try {
            return parcel.readParcelable(classLoader);
        } catch (BadParcelableException e) {
			Log.w(TAG, "Bad parcelable detected. Removing from cache", e);
			remove(key);
			return null;
		} catch (Exception e) {
            Log.e(TAG, "Failed to read parcelable", e);
            return null;
        } finally {
            parcel.recycle();
        }
	}

	private Parcel getParcel(String normalizedKey) {
		try (DiskLruCache.Snapshot snapshot = cache.get(normalizedKey)) {
			if (snapshot == null) {
				return null;
			}
			byte[] value = getBytesFromStream(snapshot);
			Parcel parcel = Parcel.obtain();
			parcel.unmarshall(value, 0, value.length);
			parcel.setDataPosition(0);
			return parcel;
		} catch (IOException e) {
			Log.e(TAG, "Failed to read parcel from cache", e);
			return null;
		}
	}

	private static byte[] getBytesFromStream(DiskLruCache.Snapshot snapshot) throws IOException {
		try (ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			 InputStream inputStream = snapshot.getInputStream(0)) {
			byte[] data = new byte[1024];
			int count;
			while ((count = inputStream.read(data, 0, data.length)) != -1) {
				buffer.write(data, 0, count);
			}
			buffer.flush();
			return buffer.toByteArray();
		}
	}

	@Override
	public void set(String key, T value) {
		String normalizedKey = normalizeKey(key);
		Parcel parcel = Parcel.obtain();
		parcel.writeParcelable(value, 0);
		try {
			saveValue(cache, parcel, normalizedKey);
		} catch (IOException e) {
			Log.e(TAG, "Failed to write parcel to cache", e);
		}
	}

	private static void saveValue(DiskLruCache cache, Parcel value, String normalizedKey) throws IOException {
		final String internString = normalizedKey.intern();
		//noinspection SynchronizationOnLocalVariableOrMethodParameter
		synchronized (internString) {
			DiskLruCache.Editor editor = cache.edit(normalizedKey);
			try {
				try (OutputStream outputStream = editor.newOutputStream(0)) {
					outputStream.write(value.marshall());
					outputStream.flush();
					editor.commit();
				}
			} finally {
				value.recycle();
			}
		}
	}

	@Override
	public boolean remove(String key) {
		String normalizedKey = normalizeKey(key);
		try {
			return cache.remove(normalizedKey);
		} catch (IOException e) {
			Log.e(TAG, "Failed to remove key from cache: " + normalizedKey, e);
			return false;
		}
	}

	@Override
	public void clear() {
		try {
			cache.delete();
		} catch (IOException e) {
			Log.e(TAG, "Failed to clear cache", e);
		}
	}

	@Override
	public boolean exists(String key) {
		String normalizedKey = normalizeKey(key);
		try (DiskLruCache.Snapshot snapshot = cache.get(normalizedKey)) {
			return snapshot != null && snapshot.getLength(0) > 0;
		} catch (IOException e) {
			Log.e(TAG, "Failed to get key from cache: " + normalizedKey, e);
			return false;
		}
	}

	private static String normalizeKey(String key) {
		String normalized = key.replaceAll("\\W+", "");
		normalized = normalized.toLowerCase();
		if (normalized.length() > MAX_KEY_SYMBOLS) {
			return normalized.substring(0, MAX_KEY_SYMBOLS);
		}
		return normalized;
	}

	@Override
	public void close() throws IOException {
		cache.close();
	}
}

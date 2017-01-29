package se.gustavkarlsson.aurora_notifier.android.caching;

import java.io.Closeable;

public interface PersistentCache<T> extends Closeable {
	T get(String key);
	void set(String key, T value);
	boolean remove(String key);
	void clear();
	boolean exists(String key);
}

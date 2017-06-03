package se.gustavkarlsson.skylight.android.cache;

public interface SingletonCache<T> {
	T get();
	void set(T value);
}

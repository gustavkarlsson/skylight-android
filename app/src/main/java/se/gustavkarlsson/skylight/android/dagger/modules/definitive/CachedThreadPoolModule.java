package se.gustavkarlsson.skylight.android.dagger.modules.definitive;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public abstract class CachedThreadPoolModule {
	public static final String CACHED_THREAD_POOL_NAME = "CachedThreadPool";

	@Provides
	@Singleton
	@Named(CACHED_THREAD_POOL_NAME)
	static ExecutorService provideCachedThreadPool() {
		return Executors.newCachedThreadPool();
	}
}

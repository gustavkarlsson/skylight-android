package se.gustavkarlsson.aurora_notifier.android.dagger.modules;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public abstract class ExecutorModule {
	public static final String CACHED_THREAD_POOL = "CachedThreadPool";

	@Provides
	@Singleton
	@Named(CACHED_THREAD_POOL)
	static ExecutorService provideExecutorService() {
		return Executors.newCachedThreadPool();
	}
}

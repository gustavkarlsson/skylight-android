package se.gustavkarlsson.skylight.android.dagger.modules

import dagger.Module
import dagger.Provides
import se.gustavkarlsson.skylight.android.dagger.CACHED_THREAD_POOL_NAME
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import javax.inject.Named
import javax.inject.Singleton

@Module
class ThreadPoolModule {

    @Provides
    @Singleton
    @Named(CACHED_THREAD_POOL_NAME)
    fun provideCachedThreadPool(): ExecutorService = Executors.newCachedThreadPool()
}

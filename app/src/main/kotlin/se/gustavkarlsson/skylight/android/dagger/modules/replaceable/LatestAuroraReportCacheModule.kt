package se.gustavkarlsson.skylight.android.dagger.modules.replaceable

import android.content.Context
import dagger.Module
import dagger.Provides
import se.gustavkarlsson.skylight.android.cache.DualAuroraReportSingletonCache
import se.gustavkarlsson.skylight.android.cache.SingletonCache
import se.gustavkarlsson.skylight.android.dagger.Names.LATEST_NAME
import se.gustavkarlsson.skylight.android.models.AuroraReport
import javax.inject.Named
import javax.inject.Singleton

@Module
class LatestAuroraReportCacheModule {

    // Published
    @Provides
    @Singleton
    @Named(LATEST_NAME)
    fun provideLatestAuroraReportCache(context: Context): SingletonCache<AuroraReport> {
        return DualAuroraReportSingletonCache(context, "latest-aurora-report")
    }

}

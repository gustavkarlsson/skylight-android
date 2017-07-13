package se.gustavkarlsson.skylight.android.dagger.modules.replaceable

import android.content.Context
import dagger.Module
import dagger.Provides
import se.gustavkarlsson.skylight.android.services.impl.auroraReportCacheSerializer
import se.gustavkarlsson.skylight.android.dagger.LATEST_NAME
import se.gustavkarlsson.skylight.android.entities.AuroraReport
import se.gustavkarlsson.skylight.android.services.SingletonCache
import se.gustavkarlsson.skylight.android.services.impl.DualSingletonCache
import javax.inject.Named
import javax.inject.Singleton

@Module
class LatestAuroraReportCacheModule {

    // Published
    @Provides
    @Singleton
    @Named(LATEST_NAME)
    fun provideLatestAuroraReportCache(context: Context): SingletonCache<AuroraReport> {
        return DualSingletonCache("latest-aurora-report", AuroraReport.default, auroraReportCacheSerializer, context)
    }

}

package se.gustavkarlsson.skylight.android.dagger.modules

import android.content.Context
import dagger.Module
import dagger.Provides
import se.gustavkarlsson.skylight.android.dagger.LAST_NAME
import se.gustavkarlsson.skylight.android.entities.AuroraReport
import se.gustavkarlsson.skylight.android.services.SingletonCache
import se.gustavkarlsson.skylight.android.services_impl.cache.DualSingletonCache
import se.gustavkarlsson.skylight.android.services_impl.cache.auroraReportCacheSerializer
import javax.inject.Named
import javax.inject.Singleton

@Module
class LatestAuroraReportCacheModule {

    @Provides
    @Singleton
    @Named(LAST_NAME)
    fun provideLatestAuroraReportCache(context: Context): SingletonCache<AuroraReport> = DualSingletonCache("latest-aurora-report", AuroraReport.default, auroraReportCacheSerializer, context)

}

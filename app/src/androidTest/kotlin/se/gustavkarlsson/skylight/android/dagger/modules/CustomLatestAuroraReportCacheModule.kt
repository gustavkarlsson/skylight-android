package se.gustavkarlsson.skylight.android.dagger.modules

import dagger.Module
import dagger.Provides
import se.gustavkarlsson.skylight.android.dagger.LAST_NAME
import se.gustavkarlsson.skylight.android.entities.AuroraReport
import se.gustavkarlsson.skylight.android.services.SingletonCache
import javax.inject.Named
import javax.inject.Singleton

@Module
class CustomLatestAuroraReportCacheModule(
	private val cache: SingletonCache<AuroraReport>
) {

    @Provides
    @Singleton
    @Named(LAST_NAME)
    fun provideLatestAuroraReportCache(): SingletonCache<AuroraReport> = cache

}

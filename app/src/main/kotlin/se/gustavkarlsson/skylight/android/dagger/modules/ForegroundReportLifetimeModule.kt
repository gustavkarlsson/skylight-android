package se.gustavkarlsson.skylight.android.dagger.modules

import dagger.Module
import dagger.Provides
import dagger.Reusable
import org.threeten.bp.Duration
import se.gustavkarlsson.skylight.android.dagger.REPORT_LIFETIME_NAME
import javax.inject.Named

@Module
class ForegroundReportLifetimeModule {

    @Provides
    @Reusable
    @Named(REPORT_LIFETIME_NAME)
    fun provideForegroundReportLifetime(): Duration = Duration.ofMinutes(15)

}

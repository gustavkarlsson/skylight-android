package se.gustavkarlsson.skylight.android.dagger.modules.replaceable

import dagger.Module
import dagger.Provides
import dagger.Reusable
import org.threeten.bp.Duration
import se.gustavkarlsson.skylight.android.dagger.Names.FOREGROUND_REPORT_LIFETIME_NAME
import javax.inject.Named

@Module
class ForegroundReportLifetimeModule {

    // Published
    @Provides
    @Reusable
    @Named(FOREGROUND_REPORT_LIFETIME_NAME)
    fun provideForegroundReportLifetime(): Duration {
        return Duration.ofMinutes(15)
    }

}

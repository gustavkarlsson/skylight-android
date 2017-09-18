package se.gustavkarlsson.skylight.android.dagger.modules

import dagger.Module
import dagger.Provides
import dagger.Reusable
import org.threeten.bp.Duration
import se.gustavkarlsson.skylight.android.dagger.UPDATE_SCHEDULER_FLEX_NAME
import se.gustavkarlsson.skylight.android.dagger.UPDATE_SCHEDULER_INTERVAL_NAME
import se.gustavkarlsson.skylight.android.services.Scheduler
import se.gustavkarlsson.skylight.android.services_impl.scheduling.GetLatestAuroraReportScheduler
import javax.inject.Named
import javax.inject.Singleton

@Module
class UpdateSchedulerModule {

    // Published
    @Provides
    @Singleton
    fun provideUpdateScheduler(@Named(UPDATE_SCHEDULER_INTERVAL_NAME) scheduleInterval: Duration, @Named(UPDATE_SCHEDULER_FLEX_NAME) scheduleFlex: Duration): Scheduler = GetLatestAuroraReportScheduler(scheduleInterval, scheduleFlex)

    @Provides
    @Reusable
    @Named(UPDATE_SCHEDULER_INTERVAL_NAME)
    fun provideUpdateSchedulerInterval(): Duration = Duration.ofMinutes(20)

    @Provides
    @Reusable
    @Named(UPDATE_SCHEDULER_FLEX_NAME)
    fun provideUpdateSchedulerFlex(): Duration = Duration.ofMinutes(10)

}

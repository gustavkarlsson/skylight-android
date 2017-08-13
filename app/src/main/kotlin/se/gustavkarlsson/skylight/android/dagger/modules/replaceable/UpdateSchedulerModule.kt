package se.gustavkarlsson.skylight.android.dagger.modules.replaceable

import dagger.Module
import dagger.Provides
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
	fun provideUpdateScheduler(@Named(UPDATE_SCHEDULER_INTERVAL_NAME) scheduleInterval: Duration, @Named(UPDATE_SCHEDULER_FLEX_NAME) scheduleFlex: Duration): Scheduler {
		return GetLatestAuroraReportScheduler(scheduleInterval, scheduleFlex)
	}

    @Provides
    @Singleton
    @Named(UPDATE_SCHEDULER_INTERVAL_NAME)
    fun provideUpdateSchedulerInterval(): Duration {
        return Duration.ofMinutes(20)
    }

    @Provides
    @Singleton
    @Named(UPDATE_SCHEDULER_FLEX_NAME)
    fun provideUpdateSchedulerFlex(): Duration {
        return Duration.ofMinutes(10)
    }

}

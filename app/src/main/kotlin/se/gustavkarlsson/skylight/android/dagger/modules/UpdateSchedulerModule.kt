package se.gustavkarlsson.skylight.android.dagger.modules

import dagger.Module
import dagger.Provides
import org.threeten.bp.Duration
import se.gustavkarlsson.skylight.android.services.Scheduler
import se.gustavkarlsson.skylight.android.services_impl.scheduling.GetLatestAuroraReportScheduler
import javax.inject.Singleton

@Module
class UpdateSchedulerModule {

	// Published
	@Provides
	@Singleton
	fun provideUpdateScheduler(): Scheduler =
		GetLatestAuroraReportScheduler(SCHEDULE_INTERVAL, SCHEDULE_FLEX)

	companion object {
		private val SCHEDULE_INTERVAL: Duration = Duration.ofMinutes(20)
		private val SCHEDULE_FLEX: Duration = Duration.ofMinutes(10)
	}
}

package se.gustavkarlsson.skylight.android.di.modules

import org.threeten.bp.Duration
import se.gustavkarlsson.skylight.android.services.Scheduler
import se.gustavkarlsson.skylight.android.services_impl.scheduling.GetLatestAuroraReportScheduler

class RealUpdateSchedulerModule : UpdateSchedulerModule {

	override val updateScheduler: Scheduler by lazy {
		GetLatestAuroraReportScheduler(SCHEDULE_INTERVAL, SCHEDULE_FLEX)
	}

	// TODO Make configurable in cosntructor
	companion object {
		private val SCHEDULE_INTERVAL: Duration = Duration.ofMinutes(20)
		private val SCHEDULE_FLEX: Duration = Duration.ofMinutes(10)
	}
}

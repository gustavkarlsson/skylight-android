package se.gustavkarlsson.skylight.android.di.modules

import se.gustavkarlsson.skylight.android.extensions.minutes
import se.gustavkarlsson.skylight.android.services.Scheduler
import se.gustavkarlsson.skylight.android.services_impl.scheduling.GetLatestAuroraReportScheduler

class RealUpdateSchedulerModule : UpdateSchedulerModule {

	override val updateScheduler: Scheduler by lazy {
		GetLatestAuroraReportScheduler(SCHEDULE_INTERVAL, SCHEDULE_FLEX)
	}

	// TODO Make configurable in cosntructor
	companion object {
		private val SCHEDULE_INTERVAL = 20.minutes
		private val SCHEDULE_FLEX = 10.minutes
	}
}

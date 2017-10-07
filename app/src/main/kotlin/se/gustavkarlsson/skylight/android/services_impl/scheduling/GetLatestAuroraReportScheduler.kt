package se.gustavkarlsson.skylight.android.services_impl.scheduling

import com.evernote.android.job.JobManager
import com.evernote.android.job.JobRequest
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.debug
import org.threeten.bp.Duration
import se.gustavkarlsson.skylight.android.services.Scheduler
import se.gustavkarlsson.skylight.android.services_impl.scheduling.UpdateJob.Companion.UPDATE_JOB_TAG

class GetLatestAuroraReportScheduler(
	private val scheduleInterval: Duration,
	private val scheduleFlex: Duration
) : Scheduler, AnkoLogger {

	override fun schedule() {
		if(!isScheduled()) {
			JobRequest.Builder(UPDATE_JOB_TAG)
				.setPeriodic(scheduleInterval.toMillis(), scheduleFlex.toMillis())
				.setUpdateCurrent(true)
				.setRequiredNetworkType(JobRequest.NetworkType.CONNECTED)
				.setRequirementsEnforced(true)
				.build()
				.schedule()
			debug("Scheduling update to run periodically")
		}
	}

	override fun unschedule() {
		if (isScheduled()) {
			JobManager.instance().cancelAllForTag(UPDATE_JOB_TAG)
		}
	}

	private fun isScheduled(): Boolean  {
		val jobRequests = JobManager.instance().getAllJobRequestsForTag(UPDATE_JOB_TAG)
		return !jobRequests.isEmpty()
	}
}

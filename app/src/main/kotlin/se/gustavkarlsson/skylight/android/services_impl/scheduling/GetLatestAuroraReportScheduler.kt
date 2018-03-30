package se.gustavkarlsson.skylight.android.services_impl.scheduling

import com.evernote.android.job.JobManager
import com.evernote.android.job.JobRequest
import org.threeten.bp.Duration
import se.gustavkarlsson.skylight.android.services.Scheduler
import se.gustavkarlsson.skylight.android.services_impl.scheduling.UpdateJob.Companion.UPDATE_JOB_TAG
import timber.log.Timber

class GetLatestAuroraReportScheduler(
	private val scheduleInterval: Duration,
	private val scheduleFlex: Duration
) : Scheduler {

	override fun schedule() {
		if(!isScheduled()) {
			JobRequest.Builder(UPDATE_JOB_TAG)
				.setPeriodic(scheduleInterval.toMillis(), scheduleFlex.toMillis())
				.setUpdateCurrent(true)
				.setRequiredNetworkType(JobRequest.NetworkType.CONNECTED)
				.setRequirementsEnforced(true)
				.build()
				.schedule()
			Timber.d("Scheduling periodic updates")
		}
	}

	override fun unschedule() {
		if (isScheduled()) {
			JobManager.instance().cancelAllForTag(UPDATE_JOB_TAG)
			Timber.d("Unscheduling periodic updates")
		}
	}

	private fun isScheduled(): Boolean  {
		val jobRequests = JobManager.instance().getAllJobRequestsForTag(UPDATE_JOB_TAG)
		val scheduled = !jobRequests.isEmpty()
		Timber.d(if (scheduled) "updates are scheduled" else "updates are not scheduled")
		return scheduled
	}
}

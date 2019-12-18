package se.gustavkarlsson.skylight.android.feature.background.scheduling

import com.evernote.android.job.JobManager
import com.evernote.android.job.JobRequest
import org.threeten.bp.Duration
import se.gustavkarlsson.skylight.android.feature.background.scheduling.NotifyJob.Companion.NOTIFY_JOB_TAG
import timber.log.Timber

internal class NotifyScheduler(
	private val scheduleInterval: Duration,
	private val scheduleFlex: Duration
) : Scheduler {

	override fun schedule() {
		if (!isScheduled()) {
			JobRequest.Builder(NOTIFY_JOB_TAG)
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
			JobManager.instance().cancelAllForTag(NOTIFY_JOB_TAG)
			Timber.d("Unscheduling periodic updates")
		}
	}

	private fun isScheduled(): Boolean {
		val jobRequests = JobManager.instance().getAllJobRequestsForTag(NOTIFY_JOB_TAG)
		val scheduled = jobRequests.isNotEmpty()
		Timber.d(if (scheduled) "updates are scheduled" else "updates are not scheduled")
		return scheduled
	}
}

package se.gustavkarlsson.skylight.android.background

import com.evernote.android.job.JobManager
import com.evernote.android.job.JobRequest
import dagger.Reusable
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.debug
import org.threeten.bp.Duration
import se.gustavkarlsson.skylight.android.dagger.UPDATE_SCHEDULER_FLEX_NAME
import se.gustavkarlsson.skylight.android.dagger.UPDATE_SCHEDULER_INTERVAL_NAME
import se.gustavkarlsson.skylight.android.settings.Settings
import javax.inject.Inject
import javax.inject.Named

@Reusable
class UpdateScheduler
@Inject
constructor(
		private val settings: Settings,
		@param:Named(UPDATE_SCHEDULER_INTERVAL_NAME) private val scheduleInterval: Duration,
		@param:Named(UPDATE_SCHEDULER_FLEX_NAME) private val scheduleFlex: Duration
) : AnkoLogger {

    fun setupBackgroundUpdates() {
        val enabled = settings.isEnableNotifications
        val jobScheduled = isScheduled()
        if (enabled && !jobScheduled) {
            scheduleJob()
        } else if (!enabled && jobScheduled) {
            cancelBackgroundUpdates()
        }
    }

    private fun scheduleJob() {
        JobRequest.Builder(UPDATE_JOB_TAG)
                .setPeriodic(scheduleInterval.toMillis(), scheduleFlex.toMillis())
                .setPersisted(true)
                .setUpdateCurrent(true)
                .setRequiredNetworkType(JobRequest.NetworkType.CONNECTED)
                .setRequirementsEnforced(true)
                .build()
                .schedule()
        debug("Scheduling update to run periodically")
    }
	private fun isScheduled(): Boolean  {
			val jobRequests = JobManager.instance().getAllJobRequestsForTag(UPDATE_JOB_TAG)
			return !jobRequests.isEmpty()
		}

    fun cancelBackgroundUpdates() {
        JobManager.instance().cancelAllForTag(UPDATE_JOB_TAG)
    }

}

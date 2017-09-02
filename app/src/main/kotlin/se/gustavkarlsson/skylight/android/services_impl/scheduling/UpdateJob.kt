package se.gustavkarlsson.skylight.android.services_impl.scheduling

import android.app.NotificationManager
import android.content.pm.PackageManager
import android.support.v4.app.NotificationCompat
import android.support.v4.content.ContextCompat
import com.evernote.android.job.Job
import com.evernote.android.job.Job.Result.FAILURE
import com.evernote.android.job.Job.Result.SUCCESS
import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.actions.PresentNewAuroraReport
import se.gustavkarlsson.skylight.android.gui.activities.AuroraRequirementsCheckingActivity.Companion.LOCATION_PERMISSION
import se.gustavkarlsson.skylight.android.services.Scheduler
import javax.inject.Inject

class UpdateJob
@Inject
constructor(
        private val notificationManager: NotificationManager,
        private val updateScheduler: Scheduler,
        private val presentNewAuroraReport: PresentNewAuroraReport
) : Job() {

    override fun onRunJob(params: Job.Params): Job.Result {
        if (!hasLocationPermission()) {
            updateScheduler.unschedule()
            sendLocationPermissionMissingNotification(notificationManager)
            return FAILURE
        }
        presentNewAuroraReport()
        return SUCCESS
    }

    private fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(context, LOCATION_PERMISSION) == PackageManager.PERMISSION_GRANTED
    }

    // TODO Replace with AuroraReportNotifier
    private fun sendLocationPermissionMissingNotification(notificationManager: NotificationManager) {
        val context = context

        @Suppress("UsePropertyAccessSyntax")
        val notification = NotificationCompat.Builder(context).run {
            setSmallIcon(R.drawable.app_logo_small)
            setContentTitle(context.getString(R.string.error_aurora_notifications_disabled_title))
            setContentText(context.getString(R.string.error_aurora_notifications_disabled_content))
            setCategory(NotificationCompat.CATEGORY_ERROR)
            setAutoCancel(true)
            setPriority(NotificationCompat.PRIORITY_HIGH)
            setDefaults(NotificationCompat.DEFAULT_ALL)
            build()
        }

        notificationManager.notify(24656, notification)
    }

	companion object {
        val UPDATE_JOB_TAG = "UPDATE_JOB"
	}
}

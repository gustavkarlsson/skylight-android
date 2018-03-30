package se.gustavkarlsson.skylight.android.services_impl.scheduling

import com.evernote.android.job.Job
import com.evernote.android.job.Job.Result.FAILURE
import com.evernote.android.job.Job.Result.SUCCESS
import io.reactivex.Single
import se.gustavkarlsson.skylight.android.entities.AuroraReport
import se.gustavkarlsson.skylight.android.services.Notifier
import se.gustavkarlsson.skylight.android.services_impl.notifications.AuroraReportNotificationDecider
import timber.log.Timber
import javax.inject.Inject

class UpdateJob
@Inject
constructor(
	private val auroraReportSingle: Single<AuroraReport>,
	private val decider: AuroraReportNotificationDecider,
	private val notifier: Notifier<AuroraReport>
) : Job() {

	override fun onRunJob(params: Job.Params): Job.Result {
		return try {
			auroraReportSingle
				.blockingGet().let {
					if (decider.shouldNotify(it)) {
						notifier.notify(it)
						decider.onNotified(it)
					}
				}
			SUCCESS
		} catch (e: Exception) {
			Timber.e(e, "Failed to run job")
			FAILURE
		}
	}

	companion object {
		const val UPDATE_JOB_TAG = "UPDATE_JOB"
	}
}

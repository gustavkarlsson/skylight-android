package se.gustavkarlsson.skylight.android.services_impl.scheduling

import com.evernote.android.job.Job
import com.evernote.android.job.Job.Result.SUCCESS
import se.gustavkarlsson.skylight.android.actions.GetNewAuroraReport
import javax.inject.Inject

class UpdateJob
@Inject
constructor(
	private val getNewAuroraReport: GetNewAuroraReport
) : Job() {

	override fun onRunJob(params: Job.Params): Job.Result {
		getNewAuroraReport()
		return SUCCESS
	}

	companion object {
		val UPDATE_JOB_TAG = "UPDATE_JOB"
	}
}

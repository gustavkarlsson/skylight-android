package se.gustavkarlsson.skylight.android.services_impl.scheduling

import com.evernote.android.job.Job
import com.evernote.android.job.Job.Result.SUCCESS
import se.gustavkarlsson.skylight.android.actions.PresentNewAuroraReport
import javax.inject.Inject

class UpdateJob
@Inject
constructor(
        private val presentNewAuroraReport: PresentNewAuroraReport
) : Job() {

    override fun onRunJob(params: Job.Params): Job.Result {
        presentNewAuroraReport()
        return SUCCESS
    }

    companion object {
        val UPDATE_JOB_TAG = "UPDATE_JOB"
    }
}

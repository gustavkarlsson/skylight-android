package se.gustavkarlsson.skylight.android.feature.background.scheduling

import com.evernote.android.job.Job
import com.evernote.android.job.Job.Result.FAILURE
import com.evernote.android.job.Job.Result.SUCCESS
import org.threeten.bp.Duration
import se.gustavkarlsson.skylight.android.entities.CompleteAuroraReport
import se.gustavkarlsson.skylight.android.feature.background.notifications.AuroraReportNotificationDecider
import se.gustavkarlsson.skylight.android.feature.background.notifications.Notifier
import se.gustavkarlsson.skylight.android.feature.background.notifications.OutdatedEvaluator
import timber.log.Timber

internal class UpdateJob(
	private val decider: AuroraReportNotificationDecider,
	private val notifier: Notifier<CompleteAuroraReport>,
	private val outdatedEvaluator: OutdatedEvaluator,
	private val timeout: Duration
) : Job() {

	override fun onRunJob(params: Params): Result {
		return try {
			val auroraReport = getAuroraReport()
			if (decider.shouldNotify(auroraReport)) {
				notifier.notify(auroraReport)
				decider.onNotified(auroraReport)
			}
			SUCCESS
		} catch (e: Exception) {
			Timber.e(e, "Failed to run job")
			FAILURE
		}
	}

	private fun getAuroraReport(): CompleteAuroraReport {
		/*
		var bestReport = state.blockingFirst().auroraReports[Place.Current]
		if (bestReport?.isRecent == true) {
			return bestReport
		}
		bestReport = awaitBetterReport(bestReport) ?: bestReport
		return bestReport ?: throw Exception("Failed to get best aurora report")
		*/
		TODO("FIXME implement this")
	}

	private fun awaitBetterReport(currentReport: CompleteAuroraReport?): CompleteAuroraReport? {
		/*
		return state
			// FIXME fix notifications
			// FIXME What about errors?
			.mapNotNull { it.auroraReports[Place.Current] }
			.filter { it != currentReport }
			.map { optionalOf(it) }
			.timeout(timeout.toMillis(), TimeUnit.MILLISECONDS)
			.firstOrError()
			.doOnError { Timber.w(it, "Failed to get aurora report") }
			.onErrorReturnItem(Absent)
			.blockingGet()
			.value
		*/
		return null
	}

	private val CompleteAuroraReport.isRecent: Boolean
		get() = !outdatedEvaluator.isOutdated(timestamp)

	companion object {
		const val UPDATE_JOB_TAG = "UPDATE_JOB"
	}
}

package se.gustavkarlsson.skylight.android.background.scheduling

import com.evernote.android.job.Job
import com.evernote.android.job.Job.Result.FAILURE
import com.evernote.android.job.Job.Result.SUCCESS
import io.reactivex.Single
import org.threeten.bp.Duration
import se.gustavkarlsson.koptional.Absent
import se.gustavkarlsson.koptional.optionalOf
import se.gustavkarlsson.skylight.android.background.notifications.AuroraReportNotificationDecider
import se.gustavkarlsson.skylight.android.background.notifications.Notifier
import se.gustavkarlsson.skylight.android.background.notifications.OutdatedEvaluator
import se.gustavkarlsson.skylight.android.entities.AuroraReport
import se.gustavkarlsson.skylight.android.extensions.mapNotNull
import se.gustavkarlsson.skylight.android.krate.Command
import se.gustavkarlsson.skylight.android.krate.SkylightStore
import timber.log.Timber
import java.util.concurrent.TimeUnit

internal class UpdateJob(
	private val store: SkylightStore,
	private val decider: AuroraReportNotificationDecider,
	private val notifier: Notifier<AuroraReport>,
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

	private fun getAuroraReport(): AuroraReport {
		var bestReport = store.currentState.currentPlace.auroraReport
		if (bestReport?.isRecent == true) {
			return bestReport
		}
		bestReport = awaitBetterReport(bestReport) ?: bestReport
		return bestReport ?: throw Exception("Failed to get best aurora report")
	}

	private fun awaitBetterReport(currentReport: AuroraReport?): AuroraReport? {
		store.issue(Command.GetAuroraReport)
		return store.states
			.flatMapSingle {
				if (it.throwable == null) {
					Single.just(it)
				} else {
					Single.error(it.throwable)
				}
			}
			.mapNotNull { it.currentPlace.auroraReport }
			.filter { it != currentReport }
			.map { optionalOf(it) }
			.timeout(timeout.toMillis(), TimeUnit.MILLISECONDS)
			.firstOrError()
			.doOnError { Timber.w(it, "Failed to get aurora report") }
			.onErrorReturnItem(Absent)
			.blockingGet()
			.value
	}

	private val AuroraReport.isRecent: Boolean
		get() = !outdatedEvaluator.isOutdated(timestamp)

	companion object {
		const val UPDATE_JOB_TAG = "UPDATE_JOB"
	}
}

package se.gustavkarlsson.skylight.android.background.scheduling

import com.evernote.android.job.Job
import com.evernote.android.job.Job.Result.FAILURE
import com.evernote.android.job.Job.Result.SUCCESS
import io.reactivex.Single
import se.gustavkarlsson.skylight.android.background.notifications.AuroraReportNotificationDecider
import se.gustavkarlsson.skylight.android.background.notifications.Notifier
import se.gustavkarlsson.skylight.android.entities.AuroraReport
import se.gustavkarlsson.skylight.android.krate.GetAuroraReportCommand
import se.gustavkarlsson.skylight.android.krate.SkylightStore
import timber.log.Timber

internal class UpdateJob(
	private val store: SkylightStore,
	private val decider: AuroraReportNotificationDecider,
	private val notifier: Notifier<AuroraReport>
) : Job() {

	override fun onRunJob(params: Job.Params): Job.Result {
		return try {
			getAuroraReport()
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

	private fun getAuroraReport(): Single<AuroraReport> {
		return store.states
			.doOnSubscribe { store.issue(GetAuroraReportCommand) }
			.flatMapSingle {
				if (it.throwable == null) {
					Single.just(it)
				} else {
					Single.error(it.throwable)
				}
			}
			.filter { it.justFinishedRefreshing }
			.flatMapSingle {
				val auroraReport = it.auroraReport
				if (auroraReport != null) {
					Single.just(auroraReport)
				} else {
					Single.error(Exception("No aurora report after refreshing"))
				}
			}
			.firstOrError()
	}

	companion object {
		const val UPDATE_JOB_TAG = "UPDATE_JOB"
	}
}

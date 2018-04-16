package se.gustavkarlsson.skylight.android.background.di.modules

import com.evernote.android.job.JobManager
import io.reactivex.Completable
import io.reactivex.Flowable
import org.threeten.bp.Duration
import se.gustavkarlsson.skylight.android.background.scheduling.GetLatestAuroraReportScheduler
import se.gustavkarlsson.skylight.android.background.scheduling.Scheduler
import se.gustavkarlsson.skylight.android.background.scheduling.UpdateJob
import se.gustavkarlsson.skylight.android.di.modules.AuroraReportModule
import se.gustavkarlsson.skylight.android.di.modules.ContextModule
import se.gustavkarlsson.skylight.android.di.modules.SettingsModule

class EvernoteJobSchedulingModule(
	contextModule: ContextModule,
	auroraReportModule: AuroraReportModule,
	notificationModule: NotificationModule,
	settingsModule: SettingsModule,
	scheduleInterval: Duration,
	schedulerFlex: Duration
) : SchedulingModule {

	private val updateJob: UpdateJob by lazy {
		UpdateJob(
			auroraReportModule.auroraReportSingle,
			notificationModule.decider,
			notificationModule.notifier
		)
	}

	private val initiateJobManager: Completable by lazy {
		Completable.fromCallable {
			JobManager.create(contextModule.context).run {
				addJobCreator { tag ->
					when (tag) {
						UpdateJob.UPDATE_JOB_TAG -> updateJob
						else -> null
					}
				}
			}
		}
	}

	private val scheduler: Scheduler by lazy {
		GetLatestAuroraReportScheduler(
			scheduleInterval,
			schedulerFlex
		)
	}

	private val scheduleBasedOnSettings: Flowable<*> by lazy {
		settingsModule.settings.notificationsEnabledChanges
			.doOnNext { enable ->
				if (enable) {
					scheduler.schedule()
				} else {
					scheduler.unschedule()
				}
			}
	}

	override val scheduleBackgroundNotifications: Completable by lazy {
		initiateJobManager
			.andThen(scheduleBasedOnSettings)
			.lastOrError()
			.toCompletable()
	}
}
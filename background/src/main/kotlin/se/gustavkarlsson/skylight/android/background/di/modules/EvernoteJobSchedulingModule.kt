package se.gustavkarlsson.skylight.android.background.di.modules

import com.evernote.android.job.JobManager
import io.reactivex.BackpressureStrategy
import io.reactivex.Completable
import io.reactivex.Flowable
import org.threeten.bp.Duration
import se.gustavkarlsson.skylight.android.background.scheduling.GetLatestAuroraReportScheduler
import se.gustavkarlsson.skylight.android.background.scheduling.Scheduler
import se.gustavkarlsson.skylight.android.background.scheduling.UpdateJob
import se.gustavkarlsson.skylight.android.di.modules.ContextModule
import se.gustavkarlsson.skylight.android.di.modules.FluxModule
import se.gustavkarlsson.skylight.android.extensions.minutes

class EvernoteJobSchedulingModule(
	contextModule: ContextModule,
	private val fluxModule: FluxModule,
	private val notificationModule: NotificationModule,
	scheduleInterval: Duration = 20.minutes,
	schedulerFlex: Duration = 10.minutes
) : SchedulingModule {

	private val initiateJobManager: Completable by lazy {
		Completable.fromCallable {
			JobManager.create(contextModule.context).run {
				addJobCreator { tag ->
					when (tag) {
						UpdateJob.UPDATE_JOB_TAG -> UpdateJob(
							fluxModule.store,
							notificationModule.decider,
							notificationModule.notifier
						)
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
		fluxModule.store.states
			.map { it.settings.notificationsEnabled }
			.distinctUntilChanged()
			.doOnNext { enable ->
				if (enable) {
					scheduler.schedule()
				} else {
					scheduler.unschedule()
				}
			}
			.toFlowable(BackpressureStrategy.LATEST)
	}

	override val scheduleBackgroundNotifications: Completable by lazy {
		initiateJobManager
			.andThen(scheduleBasedOnSettings)
			.lastOrError()
			.toCompletable()
	}
}

package se.gustavkarlsson.skylight.android.background

import android.app.KeyguardManager
import android.app.NotificationManager
import android.content.Context
import com.evernote.android.job.JobManager
import io.reactivex.Completable
import io.reactivex.Flowable
import org.koin.dsl.module.module
import se.gustavkarlsson.skylight.android.background.notifications.*
import se.gustavkarlsson.skylight.android.background.persistence.BackgroundDatabase
import se.gustavkarlsson.skylight.android.background.persistence.NotifiedChanceRepository
import se.gustavkarlsson.skylight.android.background.persistence.RoomNotifiedChanceRepository
import se.gustavkarlsson.skylight.android.background.scheduling.GetLatestAuroraReportScheduler
import se.gustavkarlsson.skylight.android.background.scheduling.Scheduler
import se.gustavkarlsson.skylight.android.background.scheduling.UpdateJob
import se.gustavkarlsson.skylight.android.entities.AuroraReport
import se.gustavkarlsson.skylight.android.extensions.minutes
import se.gustavkarlsson.skylight.android.krate.SkylightState

val backgroundModule = module {

	single {
		get<Context>().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
	}

	single {
		get<Context>().getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
	}

	single<Notifier<AuroraReport>> {
		AuroraReportNotifier(get(), get(), get(), get(), get())
	}

	single<AuroraReportNotificationDecider> {
		AuroraReportNotificationDeciderImpl(
			get(),
			get("auroraReport"),
			get(),
			OutdatedEvaluator(get()),
			AppVisibilityEvaluator(get())
		)
	}

	single<Completable>("initiateJobManager") {
		Completable.fromCallable {
			JobManager.create(get()).run {
				addJobCreator { tag ->
					when (tag) {
						UpdateJob.UPDATE_JOB_TAG -> UpdateJob(get(), get(), get())
						else -> null
					}
				}
			}
		}
	}

	single<Scheduler> { GetLatestAuroraReportScheduler(20.minutes, 10.minutes) }

	single<Flowable<*>>("scheduleBasedOnSettings") {
		get<Flowable<SkylightState>>("state")
			.map { it.settings.notificationsEnabled }
			.distinctUntilChanged()
			.doOnNext { enable ->
				if (enable) {
					get<Scheduler>().schedule()
				} else {
					get<Scheduler>().unschedule()
				}
			}
	}

	single<Completable>("scheduleBackgroundNotifications") {
		get<Completable>("initiateJobManager")
			.andThen(get<Flowable<*>>("scheduleBasedOnSettings"))
			.lastOrError()
			.ignoreElement()

	}

	single<NotifiedChanceRepository> {
		val db = BackgroundDatabase.create(get())
		RoomNotifiedChanceRepository(db)
	}
}

package se.gustavkarlsson.skylight.android.background

import android.app.KeyguardManager
import android.app.NotificationManager
import android.content.Context
import com.evernote.android.job.JobManager
import io.reactivex.Completable
import io.reactivex.Flowable
import org.koin.dsl.module.module
import se.gustavkarlsson.skylight.android.background.notifications.AppVisibilityEvaluator
import se.gustavkarlsson.skylight.android.background.notifications.AuroraReportNotificationDecider
import se.gustavkarlsson.skylight.android.background.notifications.AuroraReportNotificationDeciderImpl
import se.gustavkarlsson.skylight.android.background.notifications.AuroraReportNotifier
import se.gustavkarlsson.skylight.android.background.notifications.NotificationChannelCreator
import se.gustavkarlsson.skylight.android.background.notifications.Notifier
import se.gustavkarlsson.skylight.android.background.notifications.OutdatedEvaluator
import se.gustavkarlsson.skylight.android.background.persistence.NotifiedChanceRepository
import se.gustavkarlsson.skylight.android.background.persistence.SharedPreferencesNotifiedChanceRepository
import se.gustavkarlsson.skylight.android.background.scheduling.GetLatestAuroraReportScheduler
import se.gustavkarlsson.skylight.android.background.scheduling.Scheduler
import se.gustavkarlsson.skylight.android.background.scheduling.UpdateJob
import se.gustavkarlsson.skylight.android.entities.AuroraReport
import se.gustavkarlsson.skylight.android.extensions.minutes
import se.gustavkarlsson.skylight.android.extensions.seconds
import se.gustavkarlsson.skylight.android.krate.SkylightState
import se.gustavkarlsson.skylight.android.krate.SkylightStore

val backgroundModule = module {

	single {
		get<Context>().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
	}

	single {
		get<Context>().getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
	}

	single("auroraNotificationChannelId") {
		"aurora"
	}

	single<Notifier<AuroraReport>> {
		AuroraReportNotifier(
			get(),
			get(),
			get("chanceLevel"),
			get("auroraReport"),
			get("activity"),
			get("auroraNotificationChannelId"),
			get()
		)
	}

	single {
		OutdatedEvaluator(get())
	}

	single<AuroraReportNotificationDecider> {
		AuroraReportNotificationDeciderImpl(
			get(),
			get("auroraReport"),
			get(),
			get(),
			AppVisibilityEvaluator(get())
		)
	}

	single<Completable>("initiateJobManager") {
		val context = get<Context>()
		val store = get<SkylightStore>()
		val decider = get<AuroraReportNotificationDecider>()
		val notifier = get<Notifier<AuroraReport>>()
		val outdatedEvaluator = get<OutdatedEvaluator>()
		Completable.fromCallable {
			JobManager.create(context).run {
				addJobCreator { tag ->
					when (tag) {
						UpdateJob.UPDATE_JOB_TAG -> {
							UpdateJob(store, decider, notifier, outdatedEvaluator, 60.seconds)
						}
						else -> null
					}
				}
			}
		}
	}

	single<Scheduler> { GetLatestAuroraReportScheduler(20.minutes, 10.minutes) }

	single<Flowable<*>>("scheduleBasedOnSettings") {
		val scheduler = get<Scheduler>()
		get<Flowable<SkylightState>>("state")
			.map { it.settings.notificationsEnabled }
			.distinctUntilChanged()
			.doOnNext { enable ->
				if (enable) {
					scheduler.schedule()
				} else {
					scheduler.unschedule()
				}
			}
	}

	single {
		NotificationChannelCreator(get(), get(), get("auroraNotificationChannelId"))
	}

	single<Completable>("createNotificationChannel") {
		val channelCreator = get<NotificationChannelCreator>()
		Completable.fromAction { channelCreator.createChannel() }
	}

	single<Completable>("scheduleBackgroundNotifications") {
		get<Completable>("createNotificationChannel")
			.andThen(get<Completable>("initiateJobManager"))
			.andThen(get<Flowable<*>>("scheduleBasedOnSettings"))
			.lastOrError()
			.ignoreElement()

	}

	single<NotifiedChanceRepository> {
		SharedPreferencesNotifiedChanceRepository(get())
	}
}

package se.gustavkarlsson.skylight.android.feature.background

import android.app.KeyguardManager
import android.app.NotificationManager
import android.content.Context
import com.evernote.android.job.JobManager
import io.reactivex.Completable
import org.koin.dsl.module.module
import se.gustavkarlsson.skylight.android.entities.AuroraReport
import se.gustavkarlsson.skylight.android.extensions.minutes
import se.gustavkarlsson.skylight.android.extensions.seconds
import se.gustavkarlsson.skylight.android.feature.background.notifications.AppVisibilityEvaluator
import se.gustavkarlsson.skylight.android.feature.background.notifications.AuroraReportNotificationDecider
import se.gustavkarlsson.skylight.android.feature.background.notifications.AuroraReportNotificationDeciderImpl
import se.gustavkarlsson.skylight.android.feature.background.notifications.AuroraReportNotifier
import se.gustavkarlsson.skylight.android.feature.background.notifications.NotificationChannelCreator
import se.gustavkarlsson.skylight.android.feature.background.notifications.Notifier
import se.gustavkarlsson.skylight.android.feature.background.notifications.OutdatedEvaluator
import se.gustavkarlsson.skylight.android.feature.background.persistence.NotifiedChanceRepository
import se.gustavkarlsson.skylight.android.feature.background.persistence.SharedPreferencesNotifiedChanceRepository
import se.gustavkarlsson.skylight.android.feature.background.scheduling.GetLatestAuroraReportScheduler
import se.gustavkarlsson.skylight.android.feature.background.scheduling.Scheduler
import se.gustavkarlsson.skylight.android.feature.background.scheduling.UpdateJob

val featureBackgroundModule = module {

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
			context = get(),
			notificationManager = get(),
			chanceLevelFormatter = get("chanceLevel"),
			chanceEvaluator = get("auroraReport"),
			activityClass = get("activity"),
			channelId = get("auroraNotificationChannelId"),
			analytics = get()
		)
	}

	single {
		OutdatedEvaluator(time = get())
	}

	single<AuroraReportNotificationDecider> {
		AuroraReportNotificationDeciderImpl(
			notifiedChanceRepository = get(),
			chanceEvaluator = get("auroraReport"),
			outdatedEvaluator = get(),
			appVisibilityEvaluator = AppVisibilityEvaluator(get())
		)
	}

	single<Completable>("initiateJobManager") {
		val context = get<Context>()
		val decider = get<AuroraReportNotificationDecider>()
		val notifier = get<Notifier<AuroraReport>>()
		val outdatedEvaluator = get<OutdatedEvaluator>()
		Completable.fromCallable {
			JobManager.create(context).run {
				addJobCreator { tag ->
					when (tag) {
						UpdateJob.UPDATE_JOB_TAG -> {
							UpdateJob(decider, notifier, outdatedEvaluator, 60.seconds)
						}
						else -> null
					}
				}
			}
		}
	}

	single<Scheduler> { GetLatestAuroraReportScheduler(20.minutes, 10.minutes) }

	single<Completable>("scheduleBasedOnSettings") {
		/*
		FIXME Implement schedule based on settings
		val scheduler = get<Scheduler>()
		get<Observable<se.gustavkarlsson.skylight.android.feature.main.knot.State>>("state")
			.map {
				val notificationsEnabled = true
				notificationsEnabled // FIXME set based on settings
			}
			.distinctUntilChanged()
			.doOnNext { enable ->
				if (enable) {
					scheduler.schedule()
				} else {
					scheduler.unschedule()
				}
			}
		*/
		Completable.complete()
	}

	single {
		val context = get<Context>()
		NotificationChannelCreator(
			notificationManager = get(),
			id = get("auroraNotificationChannelId"),
			name = context.getString(R.string.aurora_alerts_channel_name)
		)
	}

	single<Completable>("createNotificationChannel") {
		val channelCreator = get<NotificationChannelCreator>()
		Completable.fromAction { channelCreator.createChannel() }
	}

	single<Completable>("scheduleBackgroundNotifications") {
		get<Completable>("createNotificationChannel")
			.andThen(get<Completable>("initiateJobManager"))
			.andThen(get<Completable>("scheduleBasedOnSettings"))

	}

	single<NotifiedChanceRepository> {
		SharedPreferencesNotifiedChanceRepository(context = get())
	}
}

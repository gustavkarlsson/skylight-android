package se.gustavkarlsson.skylight.android.feature.background

import android.app.KeyguardManager
import android.app.NotificationManager
import android.content.Context
import io.reactivex.Completable
import org.koin.dsl.module.module
import se.gustavkarlsson.skylight.android.ModuleStarter
import se.gustavkarlsson.skylight.android.entities.TriggerLevel
import se.gustavkarlsson.skylight.android.extensions.minutes
import se.gustavkarlsson.skylight.android.feature.background.notifications.AppVisibilityEvaluator
import se.gustavkarlsson.skylight.android.feature.background.notifications.Notification
import se.gustavkarlsson.skylight.android.feature.background.notifications.NotificationChannelCreator
import se.gustavkarlsson.skylight.android.feature.background.notifications.NotificationFormatter
import se.gustavkarlsson.skylight.android.feature.background.notifications.NotificationEvaluator
import se.gustavkarlsson.skylight.android.feature.background.notifications.NotificationEvaluatorImpl
import se.gustavkarlsson.skylight.android.feature.background.notifications.Notifier
import se.gustavkarlsson.skylight.android.feature.background.notifications.NotifierImpl
import se.gustavkarlsson.skylight.android.feature.background.notifications.OutdatedEvaluator
import se.gustavkarlsson.skylight.android.feature.background.persistence.LastNotificationRepository
import se.gustavkarlsson.skylight.android.feature.background.persistence.SharedPrefsLastNotificationRepository
import se.gustavkarlsson.skylight.android.feature.background.scheduling.NotifyScheduler
import se.gustavkarlsson.skylight.android.feature.background.scheduling.Scheduler
import se.gustavkarlsson.skylight.android.feature.background.scheduling.createNotifyWork
import se.gustavkarlsson.skylight.android.services.Formatter
import se.gustavkarlsson.skylight.android.services.Settings
import java.io.File

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

    single<Formatter<Notification>>("notification") {
        NotificationFormatter(chanceLevelFormatter = get("chanceLevel"))
    }

    single<Notifier> {
        NotifierImpl(
            context = get(),
            notificationManager = get(),
            notificationFormatter = get("notification"),
            activityClass = get("activity"),
            channelId = get("auroraNotificationChannelId"),
            analytics = get()
        )
    }

    single {
        OutdatedEvaluator(time = get())
    }

    single<NotificationEvaluator> {
        NotificationEvaluatorImpl(
            lastNotificationRepository = get(),
            outdatedEvaluator = get()
        )
    }

    single {
        AppVisibilityEvaluator(keyguardManager = get())
    }

    single("notify") {
        createNotifyWork(
            settings = get(),
            appVisibilityEvaluator = get(),
            locationProvider = get(),
            reportProvider = get(),
            chanceEvaluator = get("completeAuroraReport"),
            notificationEvaluator = get(),
            notifier = get(),
            time = get()
        )
    }

    single<Scheduler> {
        NotifyScheduler(
            appContext = get(),
            scheduleInterval = 15.minutes
        )
    }

    single<Completable>("scheduleBasedOnSettings") {
        val scheduler = get<Scheduler>()
        val settings = get<Settings>()
        settings.streamNotificationTriggerLevels()
            .map {
                it.any { (_, triggerLevel) ->
                    triggerLevel != TriggerLevel.NEVER
                }
            }
            .distinctUntilChanged()
            .doOnNext { enable -> if (enable) scheduler.schedule() else scheduler.unschedule() }
            .ignoreElements()
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
            .andThen(get<Completable>("scheduleBasedOnSettings"))
    }

    single<LastNotificationRepository> {
        SharedPrefsLastNotificationRepository(context = get())
    }

    single<ModuleStarter>("background") {
        val context = get<Context>()
        val scheduleBackgroundNotifications = get<Completable>("scheduleBackgroundNotifications")
        object : ModuleStarter {
            override fun start() {
                deleteOldNotifiedPrefsFile(context)
                scheduleBackgroundNotifications.subscribe()
            }
        }
    }
}

private fun deleteOldNotifiedPrefsFile(context: Context) {
    File(context.filesDir.parent + "/shared_prefs/notified_chance.xml").delete()
}

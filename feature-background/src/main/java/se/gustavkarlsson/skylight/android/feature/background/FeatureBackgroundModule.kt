package se.gustavkarlsson.skylight.android.feature.background

import android.app.Activity
import android.app.KeyguardManager
import android.app.NotificationManager
import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.Reusable
import dagger.multibindings.IntoSet
import io.reactivex.Completable
import se.gustavkarlsson.skylight.android.ModuleStarter
import se.gustavkarlsson.skylight.android.entities.ChanceLevel
import se.gustavkarlsson.skylight.android.entities.CompleteAuroraReport
import se.gustavkarlsson.skylight.android.entities.TriggerLevel
import se.gustavkarlsson.skylight.android.extensions.minutes
import se.gustavkarlsson.skylight.android.feature.background.notifications.AppVisibilityEvaluator
import se.gustavkarlsson.skylight.android.feature.background.notifications.Notification
import se.gustavkarlsson.skylight.android.feature.background.notifications.NotificationChannelCreator
import se.gustavkarlsson.skylight.android.feature.background.notifications.NotificationEvaluator
import se.gustavkarlsson.skylight.android.feature.background.notifications.NotificationEvaluatorImpl
import se.gustavkarlsson.skylight.android.feature.background.notifications.NotificationFormatter
import se.gustavkarlsson.skylight.android.feature.background.notifications.Notifier
import se.gustavkarlsson.skylight.android.feature.background.notifications.NotifierImpl
import se.gustavkarlsson.skylight.android.feature.background.notifications.OutdatedEvaluator
import se.gustavkarlsson.skylight.android.feature.background.persistence.LastNotificationRepository
import se.gustavkarlsson.skylight.android.feature.background.persistence.SharedPrefsLastNotificationRepository
import se.gustavkarlsson.skylight.android.feature.background.scheduling.NotifyScheduler
import se.gustavkarlsson.skylight.android.feature.background.scheduling.Scheduler
import se.gustavkarlsson.skylight.android.feature.background.scheduling.createNotifyWork
import se.gustavkarlsson.skylight.android.lib.analytics.Analytics
import se.gustavkarlsson.skylight.android.lib.settings.Settings
import se.gustavkarlsson.skylight.android.lib.time.Time
import se.gustavkarlsson.skylight.android.services.AuroraReportProvider
import se.gustavkarlsson.skylight.android.services.ChanceEvaluator
import se.gustavkarlsson.skylight.android.services.Formatter
import se.gustavkarlsson.skylight.android.services.LocationProvider
import java.io.File
import javax.inject.Named
import javax.inject.Singleton

@Module
class FeatureBackgroundModule {

    @Provides
    @Reusable
    internal fun notificationManager(context: Context): NotificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    @Provides
    @Reusable
    internal fun keyguardManager(context: Context): KeyguardManager =
        context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager

    @Provides
    @Reusable
    internal fun outdatedEvaluator(time: Time): OutdatedEvaluator = OutdatedEvaluator(time)

    @Provides
    @Reusable
    internal fun appVisibilityEvaluator(keyguardManager: KeyguardManager): AppVisibilityEvaluator =
        AppVisibilityEvaluator(keyguardManager)

    @Provides
    @Reusable
    internal fun scheduler(context: Context): Scheduler =
        NotifyScheduler(context, scheduleInterval = 15.minutes)

    @Provides
    @Singleton
    internal fun lastNotificationRepository(context: Context): LastNotificationRepository =
        SharedPrefsLastNotificationRepository(context)

    @Provides
    @Reusable
    internal fun notificationChannelCreator(
        context: Context,
        notificationManager: NotificationManager
    ): NotificationChannelCreator =
        NotificationChannelCreator(
            notificationManager,
            notificationChannelId,
            context.getString(R.string.aurora_alerts_channel_name)
        )

    @Provides
    @Reusable
    internal fun notificationEvaluator(
        outdatedEvaluator: OutdatedEvaluator,
        lastNotificationRepository: LastNotificationRepository
    ): NotificationEvaluator =
        NotificationEvaluatorImpl(lastNotificationRepository, outdatedEvaluator)

    @Provides
    @Reusable
    internal fun notificationFormatter(
        chanceLevelFormatter: Formatter<ChanceLevel>
    ): Formatter<Notification> = NotificationFormatter(chanceLevelFormatter)

    @Provides
    @Reusable
    internal fun notifier(
        context: Context,
        notificationManager: NotificationManager,
        notificationFormatter: Formatter<Notification>,
        activityClass: Class<out Activity>,
        analytics: Analytics
    ): Notifier =
        NotifierImpl(
            context,
            notificationManager,
            notificationFormatter,
            activityClass,
            notificationChannelId,
            analytics
        )

    @Provides
    @Reusable
    @Named("notify")
    internal fun notify(
        settings: Settings,
        appVisibilityEvaluator: AppVisibilityEvaluator,
        locationProvider: LocationProvider,
        auroraReportProvider: AuroraReportProvider,
        notificationEvaluator: NotificationEvaluator,
        chanceEvaluator: ChanceEvaluator<CompleteAuroraReport>,
        notifier: Notifier,
        time: Time
    ): Completable =
        createNotifyWork(
            settings,
            appVisibilityEvaluator,
            locationProvider,
            auroraReportProvider,
            chanceEvaluator,
            notificationEvaluator,
            notifier,
            time
        )

    @Provides
    @Reusable
    @Named("scheduleBasedOnSettings")
    internal fun scheduleBasedOnSettings(
        scheduler: Scheduler,
        settings: Settings
    ): Completable =
        settings.streamNotificationTriggerLevels()
            .map {
                it.any { (_, triggerLevel) ->
                    triggerLevel != TriggerLevel.NEVER
                }
            }
            .distinctUntilChanged()
            .doOnNext { enable -> if (enable) scheduler.schedule() else scheduler.unschedule() }
            .ignoreElements()

    @Provides
    @Reusable
    @Named("createNotificationChannel")
    internal fun createNotificationChannel(
        notificationChannelCreator: NotificationChannelCreator
    ): Completable = Completable.fromAction { notificationChannelCreator.createChannel() }

    @Provides
    @Reusable
    @Named("scheduleBackgroundNotifications")
    internal fun scheduleBackgroundNotifications(
        @Named("createNotificationChannel") createNotificationChannel: Completable,
        @Named("scheduleBasedOnSettings") scheduleBasedOnSettings: Completable
    ): Completable = createNotificationChannel.andThen(scheduleBasedOnSettings)

    @Provides
    @Reusable
    @IntoSet
    internal fun moduleStarter(
        context: Context,
        @Named("scheduleBackgroundNotifications") scheduleBackgroundNotifications: Completable
    ): ModuleStarter =
        object : ModuleStarter {
            override fun start() {
                deleteOldNotifiedPrefsFile(context)
                scheduleBackgroundNotifications.subscribe()
                // TODO Handle disposable
            }
        }
}

private fun deleteOldNotifiedPrefsFile(context: Context) {
    val file = File(context.applicationInfo.dataDir, "shared_prefs/notified_chance.xml")
    file.delete()
}

private const val notificationChannelId = "aurora"

package se.gustavkarlsson.skylight.android.feature.background

import android.app.Activity
import android.app.KeyguardManager
import android.app.NotificationManager
import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.Reusable
import dagger.multibindings.IntoSet
import kotlinx.coroutines.CoroutineScope
import se.gustavkarlsson.skylight.android.core.AppScope
import se.gustavkarlsson.skylight.android.core.Global
import se.gustavkarlsson.skylight.android.core.ModuleStarter
import se.gustavkarlsson.skylight.android.core.entities.ChanceLevel
import se.gustavkarlsson.skylight.android.core.services.ChanceEvaluator
import se.gustavkarlsson.skylight.android.core.services.Formatter
import se.gustavkarlsson.skylight.android.core.utils.minutes
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
import se.gustavkarlsson.skylight.android.feature.background.scheduling.BackgroundWork
import se.gustavkarlsson.skylight.android.feature.background.scheduling.BackgroundWorkImpl
import se.gustavkarlsson.skylight.android.feature.background.scheduling.NotifyScheduler
import se.gustavkarlsson.skylight.android.feature.background.scheduling.Scheduler
import se.gustavkarlsson.skylight.android.lib.analytics.Analytics
import se.gustavkarlsson.skylight.android.lib.aurora.AuroraReportProvider
import se.gustavkarlsson.skylight.android.lib.aurora.CompleteAuroraReport
import se.gustavkarlsson.skylight.android.lib.location.LocationProvider
import se.gustavkarlsson.skylight.android.lib.places.PlacesRepository
import se.gustavkarlsson.skylight.android.lib.settings.Settings
import se.gustavkarlsson.skylight.android.lib.time.Time

@Module
object FeatureBackgroundModule {

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
    @AppScope
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
            NOTIFICATION_CHANNEL_ID,
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
            NOTIFICATION_CHANNEL_ID,
            analytics
        )

    @Provides
    @Reusable
    @IntoSet
    internal fun moduleStarter(
        context: Context,
        scheduler: Scheduler,
        settings: Settings,
        notificationChannelCreator: NotificationChannelCreator,
        @Global globalScope: CoroutineScope,
    ): ModuleStarter =
        BackgroundModuleStarter(
            context = context,
            scheduler = scheduler,
            settings = settings,
            notificationChannelCreator = notificationChannelCreator,
            globalScope = globalScope,
        )

    @Provides
    @Reusable
    internal fun backgroundWork(
        placesRepository: PlacesRepository,
        settings: Settings,
        appVisibilityEvaluator: AppVisibilityEvaluator,
        locationProvider: LocationProvider,
        reportProvider: AuroraReportProvider,
        notificationEvaluator: NotificationEvaluator,
        chanceEvaluator: ChanceEvaluator<CompleteAuroraReport>,
        notifier: Notifier,
        time: Time
    ): BackgroundWork =
        BackgroundWorkImpl(
            placesRepository = placesRepository,
            settings = settings,
            appVisibilityEvaluator = appVisibilityEvaluator,
            locationProvider = locationProvider,
            reportProvider = reportProvider,
            notificationEvaluator = notificationEvaluator,
            chanceEvaluator = chanceEvaluator,
            notifier = notifier,
            time = time
        )
}

private const val NOTIFICATION_CHANNEL_ID = "aurora"

package se.gustavkarlsson.skylight.android.feature.background

import android.app.KeyguardManager
import android.app.NotificationManager
import android.content.Context
import androidx.core.content.getSystemService
import dagger.Module
import dagger.Provides
import dagger.Reusable
import dagger.multibindings.IntoSet
import se.gustavkarlsson.skylight.android.core.ModuleStarter
import se.gustavkarlsson.skylight.android.core.services.Formatter
import se.gustavkarlsson.skylight.android.feature.background.notifications.Notification
import se.gustavkarlsson.skylight.android.feature.background.notifications.NotificationEvaluator
import se.gustavkarlsson.skylight.android.feature.background.notifications.NotificationEvaluatorImpl
import se.gustavkarlsson.skylight.android.feature.background.notifications.NotificationFormatter
import se.gustavkarlsson.skylight.android.feature.background.notifications.Notifier
import se.gustavkarlsson.skylight.android.feature.background.notifications.NotifierImpl
import se.gustavkarlsson.skylight.android.feature.background.persistence.LastNotificationRepository
import se.gustavkarlsson.skylight.android.feature.background.persistence.SharedPrefsLastNotificationRepository
import se.gustavkarlsson.skylight.android.feature.background.scheduling.BackgroundWork
import se.gustavkarlsson.skylight.android.feature.background.scheduling.BackgroundWorkImpl
import se.gustavkarlsson.skylight.android.feature.background.scheduling.NotifyScheduler
import se.gustavkarlsson.skylight.android.feature.background.scheduling.Scheduler
import javax.inject.Qualifier

@Module
object FeatureBackgroundModule {

    @Provides
    @Reusable
    internal fun notificationManager(context: Context): NotificationManager =
        context.getSystemService()!!

    @Provides
    @Reusable
    internal fun keyguardManager(context: Context): KeyguardManager =
        context.getSystemService()!!

    @Provides
    internal fun scheduler(impl: NotifyScheduler): Scheduler = impl

    @Provides
    internal fun lastNotificationRepository(
        impl: SharedPrefsLastNotificationRepository,
    ): LastNotificationRepository = impl

    @Provides
    @AuroraAlertsChannelName
    internal fun auroraAlertsChannelName(
        context: Context,
    ): String = context.getString(R.string.aurora_alerts_channel_name)

    @Provides
    @AuroraAlertsChannelId
    internal fun auroraAlertsChannelId(): String = "aurora"

    @Provides
    internal fun notificationEvaluator(impl: NotificationEvaluatorImpl): NotificationEvaluator = impl

    @Provides
    internal fun notificationFormatter(impl: NotificationFormatter): Formatter<Notification> = impl

    @Provides
    internal fun notifier(impl: NotifierImpl): Notifier = impl

    @Provides
    @IntoSet
    internal fun moduleStarter(impl: BackgroundModuleStarter): ModuleStarter = impl

    @Provides
    internal fun backgroundWork(impl: BackgroundWorkImpl): BackgroundWork = impl
}

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class AuroraAlertsChannelName

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class AuroraAlertsChannelId

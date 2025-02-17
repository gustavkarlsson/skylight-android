package se.gustavkarlsson.skylight.android.feature.background

import android.app.KeyguardManager
import android.app.NotificationManager
import android.content.Context
import androidx.core.content.getSystemService
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides
import me.tatarka.inject.annotations.Qualifier
import se.gustavkarlsson.skylight.android.core.CoreComponent
import se.gustavkarlsson.skylight.android.core.ModuleStarter
import se.gustavkarlsson.skylight.android.core.services.Formatter
import se.gustavkarlsson.skylight.android.feature.background.notifications.Notification
import se.gustavkarlsson.skylight.android.feature.background.notifications.NotificationEvaluator
import se.gustavkarlsson.skylight.android.feature.background.notifications.NotificationEvaluatorImpl
import se.gustavkarlsson.skylight.android.feature.background.notifications.NotificationFormatter
import se.gustavkarlsson.skylight.android.feature.background.notifications.Notifier
import se.gustavkarlsson.skylight.android.feature.background.notifications.NotifierImpl
import se.gustavkarlsson.skylight.android.feature.background.persistence.DataStoreLastNotificationRepository
import se.gustavkarlsson.skylight.android.feature.background.persistence.LastNotificationRepository
import se.gustavkarlsson.skylight.android.feature.background.scheduling.BackgroundWork
import se.gustavkarlsson.skylight.android.feature.background.scheduling.BackgroundWorkImpl
import se.gustavkarlsson.skylight.android.feature.background.scheduling.NotifyScheduler
import se.gustavkarlsson.skylight.android.feature.background.scheduling.Scheduler
import se.gustavkarlsson.skylight.android.lib.aurora.AuroraComponent
import se.gustavkarlsson.skylight.android.lib.location.LocationComponent
import se.gustavkarlsson.skylight.android.lib.places.PlacesComponent
import se.gustavkarlsson.skylight.android.lib.settings.LibSettingsComponent
import se.gustavkarlsson.skylight.android.lib.time.TimeComponent

@Component
abstract class BackgroundComponent(
    @Component internal val coreComponent: CoreComponent,
    @Component internal val timeComponent: TimeComponent,
    @Component internal val placesComponent: PlacesComponent,
    @Component internal val settingsComponent: LibSettingsComponent,
    @Component internal val locationComponent: LocationComponent,
    @Component internal val auroraComponent: AuroraComponent,
) {

    abstract val backgroundWork: BackgroundWork

    abstract val moduleStarter: ModuleStarter

    @Provides
    internal fun backgroundWork(impl: BackgroundWorkImpl): BackgroundWork = impl

    @Provides
    internal fun moduleStarter(impl: BackgroundModuleStarter): ModuleStarter = impl

    @Provides
    internal fun notificationManager(context: Context): NotificationManager =
        context.getSystemService()!!

    @Provides
    internal fun keyguardManager(context: Context): KeyguardManager =
        context.getSystemService()!!

    @Provides
    internal fun scheduler(impl: NotifyScheduler): Scheduler = impl

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
    internal fun lastNotificationRepository(impl: DataStoreLastNotificationRepository): LastNotificationRepository =
        impl

    companion object {
        val instance: BackgroundComponent = BackgroundComponent::class.create(
            coreComponent = CoreComponent.instance,
            timeComponent = TimeComponent.instance,
            placesComponent = PlacesComponent.instance,
            settingsComponent = LibSettingsComponent.instance,
            locationComponent = LocationComponent.instance,
            auroraComponent = AuroraComponent.instance,
        )
    }
}

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class AuroraAlertsChannelName

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class AuroraAlertsChannelId

package se.gustavkarlsson.skylight.android.dagger.components

import dagger.Component
import se.gustavkarlsson.skylight.android.actions.SetupNotifications
import se.gustavkarlsson.skylight.android.dagger.modules.*
import se.gustavkarlsson.skylight.android.services_impl.scheduling.UpdateJob
import javax.inject.Singleton

@Component(modules = arrayOf(
        LatestAuroraReportCacheModule::class,
        EvaluationModule::class,
        ClockModule::class,
        UserFriendlyExceptionStreamModule::class,
		ForegroundReportLifetimeModule::class,
        UpdateSchedulerModule::class,
        NewAuroraReportProviderModule::class,
        PresentNewAuroraReportModule::class,
		SettingsModule::class,
		SetupNotificationsModule::class
))
@Singleton
interface ApplicationComponent {
    fun getSetupNotifications(): SetupNotifications
    fun getUpdateJob(): UpdateJob

    fun getMainActivityComponent(activityModule: ActivityModule): MainActivityComponent
    fun getSettingsActivityComponent(): SettingsActivityComponent
}

package se.gustavkarlsson.skylight.android.dagger.components

import dagger.Component
import se.gustavkarlsson.skylight.android.Skylight
import se.gustavkarlsson.skylight.android.dagger.modules.*
import se.gustavkarlsson.skylight.android.services_impl.scheduling.UpdateJob
import javax.inject.Singleton

@Component(modules = arrayOf(
        LatestAuroraReportCacheModule::class,
        EvaluationModule::class,
        ClockModule::class,
        LatestAuroraReportStreamModule::class,
        UserFriendlyExceptionStreamModule::class,
		ForegroundReportLifetimeModule::class,
        UpdateSchedulerModule::class,
        NewAuroraReportProviderModule::class,
        PresentNewAuroraReportModule::class,
        SetUpdateScheduleModule::class,
		SettingsModule::class,
		SetupNotificationsModule::class,
        NotifierModule::class
))
@Singleton
interface ApplicationComponent {
    fun inject(skylight: Skylight)

    fun getUpdateJob(): UpdateJob

    fun getMainActivityComponent(activityModule: ActivityModule): MainActivityComponent
    fun getSettingsActivityComponent(): SettingsActivityComponent
}

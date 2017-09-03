package se.gustavkarlsson.skylight.android.dagger.components

import dagger.Component
import org.threeten.bp.Clock
import se.gustavkarlsson.skylight.android.Skylight
import se.gustavkarlsson.skylight.android.dagger.modules.*
import se.gustavkarlsson.skylight.android.services.Scheduler
import se.gustavkarlsson.skylight.android.services.providers.AuroraReportProvider
import se.gustavkarlsson.skylight.android.services_impl.scheduling.UpdateJob
import javax.inject.Singleton

@Component(modules = arrayOf(
        LatestAuroraReportCacheModule::class,
        EvaluationModule::class,
        AuroraReportModule::class,
        ClockModule::class,
        LatestAuroraReportStreamModule::class,
        UserFriendlyExceptionStreamModule::class,
        BackgroundUpdateTimeoutModule::class,
        ForegroundUpdateTimeoutModule::class,
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

    fun getAuroraReportProvider(): AuroraReportProvider
    fun getUpdateJob(): UpdateJob
    fun getUpdateScheduler(): Scheduler
    fun getClock(): Clock

    fun getMainActivityComponent(activityModule: ActivityModule): MainActivityComponent
    fun getSettingsActivityComponent(): SettingsActivityComponent
}

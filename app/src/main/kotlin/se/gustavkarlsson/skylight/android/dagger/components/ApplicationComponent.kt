package se.gustavkarlsson.skylight.android.dagger.components

import dagger.Component
import se.gustavkarlsson.skylight.android.actions.SetupNotifications
import se.gustavkarlsson.skylight.android.dagger.modules.*
import se.gustavkarlsson.skylight.android.services_impl.scheduling.UpdateJob
import javax.inject.Singleton

@Component(modules = arrayOf(
        ContextModule::class,
        LatestAuroraReportCacheModule::class,
        LatestAuroraReportStreamModule::class,
        SettingsModule::class,
		TimeModule::class,
        NotifierModule::class,
        EvaluationModule::class,
        SetupNotificationsModule::class,
        PresentNewAuroraReportModule::class,
        NewAuroraReportProviderModule::class,
        SystemServiceModule::class,
        LocationProviderModule::class,
        AuroraFactorsModule::class,
        AsyncAddressProviderModule::class,
        GeocoderModule::class,
        UserFriendlyExceptionStreamModule::class,
        UpdateSchedulerModule::class
))
@Singleton
interface ApplicationComponent {
    fun getSetupNotifications(): SetupNotifications
    fun getUpdateJob(): UpdateJob

    fun getMainActivityComponent(activityModule: ActivityModule): MainActivityComponent
    fun getSettingsActivityComponent(): SettingsActivityComponent
}

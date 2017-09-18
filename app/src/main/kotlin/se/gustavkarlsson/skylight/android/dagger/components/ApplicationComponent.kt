package se.gustavkarlsson.skylight.android.dagger.components

import dagger.Component
import se.gustavkarlsson.skylight.android.actions.SetupNotifications
import se.gustavkarlsson.skylight.android.dagger.modules.ActivityModule
import se.gustavkarlsson.skylight.android.dagger.modules.SetupNotificationsModule
import se.gustavkarlsson.skylight.android.dagger.modules.UpdateJobModule
import se.gustavkarlsson.skylight.android.dagger.modules.UpdateSchedulerModule
import se.gustavkarlsson.skylight.android.services_impl.scheduling.UpdateJob
import javax.inject.Singleton

// TODO Get rid of all includes, and put all dependencies in component instead
@Component(modules = arrayOf(
        SetupNotificationsModule::class,
        UpdateJobModule::class,
        UpdateSchedulerModule::class
))
@Singleton
interface ApplicationComponent {
    fun getSetupNotifications(): SetupNotifications
    fun getUpdateJob(): UpdateJob

    fun getMainActivityComponent(activityModule: ActivityModule): MainActivityComponent
    fun getSettingsActivityComponent(): SettingsActivityComponent
}

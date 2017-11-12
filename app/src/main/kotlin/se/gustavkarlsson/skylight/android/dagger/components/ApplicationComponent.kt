package se.gustavkarlsson.skylight.android.dagger.components

import dagger.Component
import se.gustavkarlsson.skylight.android.actions.SetupNotifications
import se.gustavkarlsson.skylight.android.dagger.modules.*
import se.gustavkarlsson.skylight.android.services_impl.scheduling.UpdateJob
import javax.inject.Singleton

@Component(modules = arrayOf(
	ContextModule::class,
	TimeModule::class,
	SystemServiceModule::class,
	GeocoderModule::class,
	SharedPreferencesModule::class,
	SettingsModule::class,
	NotifierModule::class,
	UpdateSchedulerModule::class,
	SetupNotificationsModule::class,
	EvaluationModule::class,
	AuroraReportStreamModule::class,
	UserFriendlyExceptionStreamModule::class,
	LocationProviderModule::class,
	AuroraFactorsModule::class,
	NewAuroraReportProviderModule::class,
	LocationNameProviderModule::class,
	GetNewAuroraReportModule::class
))
@Singleton
interface ApplicationComponent {
	fun getSetupNotifications(): SetupNotifications
	fun getUpdateJob(): UpdateJob

	fun getMainActivityComponent(activityModule: ActivityModule): MainActivityComponent
	fun getSettingsActivityComponent(): SettingsActivityComponent
}

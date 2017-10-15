package se.gustavkarlsson.skylight.android.dagger.components


import dagger.Component
import se.gustavkarlsson.skylight.android.dagger.modules.*
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(
	ContextModule::class,
	LatestAuroraReportCacheModule::class,
	LatestAuroraReportStreamModule::class,
	TestSharedPreferencesModule::class,
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
	LocationNameProviderModule::class,
	GeocoderModule::class,
	UserFriendlyExceptionStreamModule::class,
	UpdateSchedulerModule::class
))
interface TestApplicationComponent : ApplicationComponent

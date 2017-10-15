package se.gustavkarlsson.skylight.android.dagger.components


import dagger.Component
import se.gustavkarlsson.skylight.android.dagger.modules.*
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(
	ContextModule::class,
	CustomLatestAuroraReportCacheModule::class,
	LatestAuroraReportStreamModule::class,
	CustomSettingsModule::class,
	TimeModule::class,
	NotifierModule::class,
	EvaluationModule::class,
	SetupNotificationsModule::class,
	PresentNewAuroraReportModule::class,
	NewAuroraReportProviderModule::class,
	SystemServiceModule::class,
	CustomLocationProviderModule::class,
	AuroraFactorsModule::class,
	CustomLocationNameProviderModule::class,
	GeocoderModule::class,
	UserFriendlyExceptionStreamModule::class,
	UpdateSchedulerModule::class
))
interface TestApplicationComponent : ApplicationComponent

package se.gustavkarlsson.skylight.android.dagger.components


import dagger.Component
import se.gustavkarlsson.skylight.android.dagger.modules.*
import se.gustavkarlsson.skylight.android.gui.activities.main.fragments.aurora_chance.AuroraChanceFragmentTest
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
	TestLocationProviderModule::class,
	AuroraFactorsModule::class,
	TestLocationNameProviderModule::class,
	GeocoderModule::class,
	UserFriendlyExceptionStreamModule::class,
	UpdateSchedulerModule::class
))
interface TestApplicationComponent : ApplicationComponent {
	fun inject(auroraChanceFragmentTest: AuroraChanceFragmentTest)
}

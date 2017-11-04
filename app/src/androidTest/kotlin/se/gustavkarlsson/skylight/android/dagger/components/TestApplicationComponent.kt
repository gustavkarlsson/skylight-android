package se.gustavkarlsson.skylight.android.dagger.components

import dagger.Component
import se.gustavkarlsson.skylight.android.dagger.modules.*
import se.gustavkarlsson.skylight.android.gui.activities.main.fragments.aurora_chance.AuroraChanceFragmentTest
import javax.inject.Singleton

@Component(modules = arrayOf(
	ContextModule::class,
	TimeModule::class,
	SystemServiceModule::class,
	GeocoderModule::class,
	TestSharedPreferencesModule::class,
	SettingsModule::class,
	NotifierModule::class,
	UpdateSchedulerModule::class,
	SetupNotificationsModule::class,
	EvaluationModule::class,
	LatestAuroraReportModule::class,
	UserFriendlyExceptionStreamModule::class,
	TestLocationProviderModule::class,
	AuroraFactorsModule::class,
	NewAuroraReportProviderModule::class,
	TestLocationNameProviderModule::class,
	PresentNewAuroraReportModule::class
))
@Singleton
interface TestApplicationComponent : ApplicationComponent {
	fun inject(auroraChanceFragmentTest: AuroraChanceFragmentTest)
}

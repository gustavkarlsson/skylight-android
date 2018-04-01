package se.gustavkarlsson.skylight.android.dagger.components

import dagger.Component
import se.gustavkarlsson.skylight.android.dagger.modules.*
import se.gustavkarlsson.skylight.android.gui.activities.main.MainActivityTest
import se.gustavkarlsson.skylight.android.gui.activities.main.fragments.aurora_chance.AuroraChanceFragmentTest
import javax.inject.Singleton

@Component(modules = [
	ContextModule::class,
	TimeModule::class,
	SystemServiceModule::class,
	TestSharedPreferencesModule::class,
	SettingsModule::class,
	NotifierModule::class,
	UpdateSchedulerModule::class,
	EvaluationModule::class,
	AuroraReportModule::class,
	TestLocationModule::class,
	AuroraFactorsModule::class,
	DarknessModule::class,
	VisibilityModule::class,
	GeomagLocationModule::class,
	KpIndexModule::class,
	TestLocationNameModule::class,
	ViewModelsModule::class,
	FormattingModule::class,
	LocalizationModule::class,
	ConnectivityModule::class
])
@Singleton
interface TestApplicationComponent : ApplicationComponent {
	fun inject(auroraChanceFragmentTest: AuroraChanceFragmentTest)
	fun inject(mainActivityTest: MainActivityTest)
}

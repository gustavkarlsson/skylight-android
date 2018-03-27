package se.gustavkarlsson.skylight.android.dagger.components

import dagger.Component
import se.gustavkarlsson.skylight.android.dagger.modules.*
import se.gustavkarlsson.skylight.android.gui.activities.main.MainViewModelFactory
import se.gustavkarlsson.skylight.android.gui.activities.main.fragments.aurora_chance.AuroraChanceViewModelFactory
import se.gustavkarlsson.skylight.android.gui.activities.main.fragments.aurora_factors.AuroraFactorsViewModelFactory
import se.gustavkarlsson.skylight.android.services.Scheduler
import se.gustavkarlsson.skylight.android.services.Settings
import se.gustavkarlsson.skylight.android.services_impl.scheduling.UpdateJob
import javax.inject.Singleton

@Component(modules = arrayOf(
	ContextModule::class,
	TimeModule::class,
	SystemServiceModule::class,
	SharedPreferencesModule::class,
	SettingsModule::class,
	NotifierModule::class,
	UpdateSchedulerModule::class,
	EvaluationModule::class,
	AuroraReportModule::class,
	CacheModule::class,
	LocationModule::class,
	AuroraFactorsModule::class,
	DarknessModule::class,
	VisibilityModule::class,
	GeomagLocationModule::class,
	KpIndexModule::class,
	LocationNameProviderModule::class,
	ViewModelsModule::class,
	FormattingModule::class,
	LocalizationModule::class
))
@Singleton
interface ApplicationComponent {
	fun getUpdateJob(): UpdateJob

	fun getAuroraChanceViewModelFactory(): AuroraChanceViewModelFactory
	fun getAuroraFactorsViewModelFactory(): AuroraFactorsViewModelFactory
	fun getMainViewModelFactory(): MainViewModelFactory
	fun getSettings(): Settings
	fun getScheduler(): Scheduler
}

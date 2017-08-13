package se.gustavkarlsson.skylight.android.dagger.components

import dagger.Component
import org.threeten.bp.Clock
import se.gustavkarlsson.skylight.android.dagger.modules.clean.NewAuroraReportProviderModule
import se.gustavkarlsson.skylight.android.dagger.modules.definitive.ActivityModule
import se.gustavkarlsson.skylight.android.dagger.modules.definitive.EvaluationModule
import se.gustavkarlsson.skylight.android.dagger.modules.definitive.LatestAuroraReportStreamModule
import se.gustavkarlsson.skylight.android.dagger.modules.definitive.UserFriendlyExceptionStreamModule
import se.gustavkarlsson.skylight.android.dagger.modules.replaceable.*
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
	SetUpdateScheduleModule::class
))
@Singleton
interface ApplicationComponent {
	fun getAuroraReportProvider(): AuroraReportProvider
	fun getUpdateJob(): UpdateJob
	fun getUpdateScheduler(): Scheduler
	fun getClock(): Clock

	fun getMainActivityComponent(activityModule: ActivityModule): MainActivityComponent
	fun getSettingsActivityComponent(): SettingsActivityComponent
}

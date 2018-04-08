package se.gustavkarlsson.skylight.android.di.modules

import se.gustavkarlsson.skylight.android.gui.activities.main.MainViewModelFactory
import se.gustavkarlsson.skylight.android.gui.activities.main.fragments.aurora_chance.AuroraChanceViewModelFactory
import se.gustavkarlsson.skylight.android.gui.activities.main.fragments.aurora_factors.AuroraFactorsViewModelFactory
import se.gustavkarlsson.skylight.android.services.Analytics
import se.gustavkarlsson.skylight.android.services.Scheduler
import se.gustavkarlsson.skylight.android.services.Settings
import se.gustavkarlsson.skylight.android.services_impl.scheduling.UpdateJob

interface AppModule {
	val settings: Settings
	val updateScheduler: Scheduler
	val updateJob: UpdateJob
	val auroraChanceViewModelFactory: AuroraChanceViewModelFactory
	val auroraFactorsViewModelFactory: AuroraFactorsViewModelFactory
	val mainViewModelFactory: MainViewModelFactory
	val analytics: Analytics
}

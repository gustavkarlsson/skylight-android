package se.gustavkarlsson.skylight.android.di.modules

import se.gustavkarlsson.skylight.android.gui.activities.main.MainViewModelFactory
import se.gustavkarlsson.skylight.android.gui.activities.main.fragments.aurora_chance.AuroraChanceViewModelFactory
import se.gustavkarlsson.skylight.android.gui.activities.main.fragments.aurora_factors.AuroraFactorsViewModelFactory

interface ViewModelsModule {
	val auroraChanceViewModelFactory: AuroraChanceViewModelFactory
	val auroraFactorsViewModelFactory: AuroraFactorsViewModelFactory
	val mainViewModelFactory: MainViewModelFactory
}

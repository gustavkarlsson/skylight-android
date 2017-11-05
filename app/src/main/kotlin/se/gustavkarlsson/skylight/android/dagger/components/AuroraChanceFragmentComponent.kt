package se.gustavkarlsson.skylight.android.dagger.components

import dagger.Subcomponent
import se.gustavkarlsson.skylight.android.dagger.modules.ChancePresenterModule
import se.gustavkarlsson.skylight.android.dagger.modules.FragmentRootViewModule
import se.gustavkarlsson.skylight.android.dagger.modules.PresentingAuroraReportsModule
import se.gustavkarlsson.skylight.android.dagger.modules.TimeSinceUpdateControllerModule
import se.gustavkarlsson.skylight.android.dagger.scopes.FragmentScope
import se.gustavkarlsson.skylight.android.gui.activities.main.fragments.aurora_chance.AuroraChanceFragment

@Subcomponent(modules = arrayOf(
	FragmentRootViewModule::class,
	TimeSinceUpdateControllerModule::class,
	ChancePresenterModule::class,
	PresentingAuroraReportsModule::class
))
@FragmentScope
interface AuroraChanceFragmentComponent {
	fun inject(auroraChanceFragment: AuroraChanceFragment)
}

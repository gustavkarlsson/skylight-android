package se.gustavkarlsson.skylight.android.dagger.components

import dagger.Subcomponent
import se.gustavkarlsson.skylight.android.dagger.modules.*
import se.gustavkarlsson.skylight.android.dagger.scopes.FragmentScope
import se.gustavkarlsson.skylight.android.gui.activities.main.fragments.aurora_chance.AuroraChanceFragment

@Subcomponent(modules = arrayOf(
		FragmentRootViewModule::class,
		LocationPresenterModule::class,
		TimeSinceUpdateControllerModule::class,
		ChancePresenterModule::class,
		AuroraReportChancePresenterModule::class,
		PresentingAuroraReportsModule::class
))
@FragmentScope
interface AuroraChanceFragmentComponent {
    fun inject(auroraChanceFragment: AuroraChanceFragment)
}

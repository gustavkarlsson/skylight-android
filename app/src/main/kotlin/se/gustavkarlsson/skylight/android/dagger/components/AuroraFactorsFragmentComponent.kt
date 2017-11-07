package se.gustavkarlsson.skylight.android.dagger.components

import dagger.Subcomponent
import se.gustavkarlsson.skylight.android.dagger.modules.FragmentRootViewModule
import se.gustavkarlsson.skylight.android.dagger.scopes.FragmentScope
import se.gustavkarlsson.skylight.android.gui.activities.main.fragments.aurora_factors.AuroraFactorFragment

@Subcomponent(modules = arrayOf(
	FragmentRootViewModule::class
))
@FragmentScope
interface AuroraFactorsFragmentComponent {
	fun inject(auroraFactorFragment: AuroraFactorFragment)
}

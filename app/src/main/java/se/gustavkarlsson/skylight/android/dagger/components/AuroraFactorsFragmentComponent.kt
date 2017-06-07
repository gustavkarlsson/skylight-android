package se.gustavkarlsson.skylight.android.dagger.components

import dagger.Subcomponent
import se.gustavkarlsson.skylight.android.dagger.modules.replaceable.*
import se.gustavkarlsson.skylight.android.dagger.scopes.FragmentScope
import se.gustavkarlsson.skylight.android.gui.activities.main.fragments.aurora_factors.AuroraFactorFragment

@Subcomponent(modules = arrayOf(
		FragmentRootViewModule::class,
		GeomagActivityPresenterModule::class,
		GeomagLocationPresenterModule::class,
		DarknessPresenterModule::class,
		VisibilityPresenterModule::class
))
@FragmentScope
internal interface AuroraFactorsFragmentComponent {
    fun inject(auroraFactorFragment: AuroraFactorFragment)
}
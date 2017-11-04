package se.gustavkarlsson.skylight.android.dagger.components

import dagger.Subcomponent
import se.gustavkarlsson.skylight.android.dagger.modules.*
import se.gustavkarlsson.skylight.android.dagger.scopes.FragmentScope
import se.gustavkarlsson.skylight.android.gui.activities.main.fragments.aurora_factors.AuroraFactorFragment

@Subcomponent(modules = arrayOf(
	FragmentRootViewModule::class,
	KpIndexPresenterModule::class,
	GeomagLocationPresenterModule::class,
	DarknessPresenterModule::class,
	VisibilityPresenterModule::class,
	AuroraReportFactorsPresenterModule::class,
	PresentingAuroraReportsModule::class
))
@FragmentScope
interface AuroraFactorsFragmentComponent {
	fun inject(auroraFactorFragment: AuroraFactorFragment)
}

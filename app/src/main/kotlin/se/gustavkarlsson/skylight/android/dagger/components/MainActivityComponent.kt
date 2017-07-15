package se.gustavkarlsson.skylight.android.dagger.components

import dagger.Subcomponent
import se.gustavkarlsson.skylight.android.dagger.modules.replaceable.*
import se.gustavkarlsson.skylight.android.dagger.scopes.ActivityScope
import se.gustavkarlsson.skylight.android.gui.activities.main.MainActivity

@Subcomponent(modules = arrayOf(
	SwipeToRefreshModule::class,
	ForegroundReportLifetimeModule::class,
	ShowNewAuroraReportModule::class,
	ShowRecentAuroraReportModule::class,
	ShowLastAuroraReportModule::class,
	ShowingErrorsModule::class
))
@ActivityScope
interface MainActivityComponent {
	fun inject(mainActivity: MainActivity)

	fun getAuroraChanceFragmentComponent(fragmentRootViewModule: FragmentRootViewModule): AuroraChanceFragmentComponent
	fun getAuroraFactorsFragmentComponent(fragmentRootViewModule: FragmentRootViewModule): AuroraFactorsFragmentComponent
}

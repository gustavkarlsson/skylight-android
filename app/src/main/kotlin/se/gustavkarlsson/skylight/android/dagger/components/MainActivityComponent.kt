package se.gustavkarlsson.skylight.android.dagger.components

import dagger.Subcomponent
import se.gustavkarlsson.skylight.android.dagger.modules.*
import se.gustavkarlsson.skylight.android.dagger.scopes.ActivityScope
import se.gustavkarlsson.skylight.android.gui.activities.main.MainActivity

@Subcomponent(modules = arrayOf(
	ActivityModule::class,
	SwipeToRefreshModule::class,
	PresentRecentAuroraReportModule::class,
	LastAuroraReportProviderModule::class,
	PresentingErrorsModule::class,
	SetUpdateScheduleModule::class,
	ForegroundReportLifetimeModule::class
))
@ActivityScope
interface MainActivityComponent {
	fun inject(mainActivity: MainActivity)

	fun getAuroraChanceFragmentComponent(fragmentRootViewModule: FragmentRootViewModule): AuroraChanceFragmentComponent
	fun getAuroraFactorsFragmentComponent(fragmentRootViewModule: FragmentRootViewModule): AuroraFactorsFragmentComponent
}

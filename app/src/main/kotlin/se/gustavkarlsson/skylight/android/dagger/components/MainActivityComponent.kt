package se.gustavkarlsson.skylight.android.dagger.components

import dagger.Subcomponent
import se.gustavkarlsson.skylight.android.dagger.modules.definitive.UpdateErrorBroadcastReceiverModule
import se.gustavkarlsson.skylight.android.dagger.modules.replaceable.ForegroundReportLifetimeModule
import se.gustavkarlsson.skylight.android.dagger.modules.replaceable.FragmentRootViewModule
import se.gustavkarlsson.skylight.android.dagger.modules.replaceable.SwipeToRefreshModule
import se.gustavkarlsson.skylight.android.dagger.scopes.ActivityScope
import se.gustavkarlsson.skylight.android.gui.activities.main.MainActivity

@Subcomponent(modules = arrayOf(
		SwipeToRefreshModule::class,
		UpdateErrorBroadcastReceiverModule::class,
		ForegroundReportLifetimeModule::class
))
@ActivityScope
interface MainActivityComponent {
    fun inject(mainActivity: MainActivity)

    fun getAuroraChanceFragmentComponent(fragmentRootViewModule: FragmentRootViewModule): AuroraChanceFragmentComponent
    fun getAuroraFactorsFragmentComponent(fragmentRootViewModule: FragmentRootViewModule): AuroraFactorsFragmentComponent
}

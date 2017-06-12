package se.gustavkarlsson.skylight.android.dagger.components

import dagger.Subcomponent
import se.gustavkarlsson.skylight.android.dagger.modules.replaceable.ChancePresenterModule
import se.gustavkarlsson.skylight.android.dagger.modules.replaceable.FragmentRootViewModule
import se.gustavkarlsson.skylight.android.dagger.modules.replaceable.LocationPresenterModule
import se.gustavkarlsson.skylight.android.dagger.modules.replaceable.TimeSinceUpdateControllerModule
import se.gustavkarlsson.skylight.android.dagger.scopes.FragmentScope
import se.gustavkarlsson.skylight.android.gui.activities.main.fragments.aurora_chance.AuroraChanceFragment

@Subcomponent(modules = arrayOf(
		FragmentRootViewModule::class,
		LocationPresenterModule::class,
		TimeSinceUpdateControllerModule::class,
		ChancePresenterModule::class
))
@FragmentScope
interface AuroraChanceFragmentComponent {
    fun inject(auroraChanceFragment: AuroraChanceFragment)
}

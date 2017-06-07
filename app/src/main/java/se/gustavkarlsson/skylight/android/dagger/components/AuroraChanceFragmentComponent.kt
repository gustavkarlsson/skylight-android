package se.gustavkarlsson.skylight.android.dagger.components

import dagger.Subcomponent
import se.gustavkarlsson.skylight.android.dagger.modules.replaceable.ChancePresenterModule
import se.gustavkarlsson.skylight.android.dagger.modules.replaceable.FragmentRootViewModule
import se.gustavkarlsson.skylight.android.dagger.modules.replaceable.LocationPresenterModule
import se.gustavkarlsson.skylight.android.dagger.modules.replaceable.TimeSinceUpdatePresenterModule
import se.gustavkarlsson.skylight.android.dagger.scopes.FragmentScope
import se.gustavkarlsson.skylight.android.gui.activities.main.fragments.aurora_chance.AuroraChanceFragment

@Subcomponent(modules = arrayOf(
		FragmentRootViewModule::class,
		LocationPresenterModule::class,
		TimeSinceUpdatePresenterModule::class,
		ChancePresenterModule::class
))
@FragmentScope
internal interface AuroraChanceFragmentComponent {
    fun inject(auroraChanceFragment: AuroraChanceFragment)
}

package se.gustavkarlsson.skylight.android.dagger.components

import dagger.Subcomponent
import se.gustavkarlsson.skylight.android.dagger.scopes.FragmentScope
import se.gustavkarlsson.skylight.android.gui.activities.settings.SettingsFragment

@Subcomponent
@FragmentScope
interface SettingsFragmentComponent {
	fun inject(settingsFragment: SettingsFragment)
}

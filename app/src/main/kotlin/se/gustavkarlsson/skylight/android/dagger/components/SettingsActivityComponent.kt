package se.gustavkarlsson.skylight.android.dagger.components

import dagger.Subcomponent
import se.gustavkarlsson.skylight.android.dagger.modules.SetUpdateScheduleModule
import se.gustavkarlsson.skylight.android.dagger.scopes.ActivityScope
import se.gustavkarlsson.skylight.android.gui.activities.settings.SettingsActivity

@Subcomponent(modules = arrayOf(
	SetUpdateScheduleModule::class
))
@ActivityScope
interface SettingsActivityComponent {
	fun inject(settingsActivity: SettingsActivity)

	fun getSettingsFragmentComponent(): SettingsFragmentComponent
}

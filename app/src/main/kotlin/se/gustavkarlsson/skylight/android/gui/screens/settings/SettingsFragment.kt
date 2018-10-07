package se.gustavkarlsson.skylight.android.gui.screens.settings

import kotlinx.android.synthetic.main.fragment_settings.toolbar
import org.koin.android.ext.android.inject
import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.extensions.enableBackNavigation
import se.gustavkarlsson.skylight.android.gui.BaseFragment
import se.gustavkarlsson.skylight.android.navigation.Navigator

class SettingsFragment : BaseFragment(R.layout.fragment_settings) {

	val navigator: Navigator by inject()

	override fun initView() {
		toolbar.setTitle(R.string.settings)
		toolbar.enableBackNavigation(navigator)
	}
}

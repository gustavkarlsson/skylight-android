package se.gustavkarlsson.skylight.android.gui.screens.settings

import androidx.appcompat.widget.Toolbar
import kotlinx.android.synthetic.main.fragment_settings.toolbar
import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.gui.BaseFragment

class SettingsFragment : BaseFragment(R.layout.fragment_settings) {

	override fun setupToolbar(): Toolbar? = toolbar
}

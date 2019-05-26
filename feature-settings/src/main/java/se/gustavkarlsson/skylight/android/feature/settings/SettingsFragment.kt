package se.gustavkarlsson.skylight.android.feature.settings

import androidx.appcompat.widget.Toolbar
import kotlinx.android.synthetic.main.fragment_settings.toolbarView
import se.gustavkarlsson.skylight.android.feature.base.BaseFragment

internal class SettingsFragment : BaseFragment() {

	override val layoutId: Int = R.layout.fragment_settings

	override val toolbar: Toolbar?
		get() = toolbarView
}

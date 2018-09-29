package se.gustavkarlsson.skylight.android.gui.screens.settings

import com.jakewharton.rxbinding2.view.clicks
import com.uber.autodispose.LifecycleScopeProvider
import com.uber.autodispose.kotlin.autoDisposable
import kotlinx.android.synthetic.main.fragment_settings.*
import org.koin.android.ext.android.inject
import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.gui.BaseFragment
import se.gustavkarlsson.skylight.android.navigation.Navigator

class SettingsFragment : BaseFragment(R.layout.fragment_settings) {

	val navigator: Navigator by inject()

	override fun bindData(scope: LifecycleScopeProvider<*>) {
		backButton.clicks()
			.autoDisposable(scope)
			.subscribe { navigator.goBack() }
	}
}

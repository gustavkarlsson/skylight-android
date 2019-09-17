package se.gustavkarlsson.skylight.android.feature.intro

import android.os.Bundle
import androidx.lifecycle.ViewModel
import com.ioki.textref.TextRef
import se.gustavkarlsson.skylight.android.lib.navigation.NavItem
import se.gustavkarlsson.skylight.android.lib.navigation.Navigator

internal class IntroViewModel(
	private val navigator: Navigator,
	private val versionManager: RunVersionManager
) : ViewModel() {
	val privacyPolicyHtml: TextRef = TextRef(R.string.html_privacy_policy_link, TextRef(R.string.privacy_policy))

	fun registerScreenSeen() = versionManager.signalRunCompleted()
	fun navigateToMain() = navigator.replace(NavItem("main"))
	fun navigateToPickPlace() {
		val arguments = Bundle().apply {
			putParcelable("destination", NavItem("main"))
		}
		navigator.replace(NavItem("addplace", arguments = arguments))
	}
}

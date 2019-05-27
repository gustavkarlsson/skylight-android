package se.gustavkarlsson.skylight.android.feature.intro

import androidx.lifecycle.ViewModel
import com.ioki.textref.TextRef
import se.gustavkarlsson.skylight.android.lib.ui.Navigator
import se.gustavkarlsson.skylight.android.services.RunVersionManager

internal class IntroViewModel(
	private val navigator: Navigator,
	private val versionManager: RunVersionManager,
	private val targetId: String
) : ViewModel() {
	val privacyPolicyHtml: TextRef = TextRef(R.string.html_privacy_policy_link, TextRef(R.string.privacy_policy))

	fun registerScreenSeen() = versionManager.signalRunCompleted()
	fun navigateForward() = navigator.navigate(targetId)
}

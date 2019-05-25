package se.gustavkarlsson.skylight.android.feature.intro

import androidx.lifecycle.ViewModel
import com.ioki.textref.TextRef
import se.gustavkarlsson.skylight.android.krate.Command
import se.gustavkarlsson.skylight.android.krate.SkylightStore

internal class IntroViewModel(
	private val store: SkylightStore
) : ViewModel() {
	val privacyPolicyHtml: TextRef = TextRef(R.string.html_privacy_policy_link, TextRef(R.string.privacy_policy))

	fun signalFirstRunCompleted() = store.issue(Command.SignalFirstRunCompleted)
}

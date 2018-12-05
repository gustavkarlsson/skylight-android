package se.gustavkarlsson.skylight.android.gui.screens.intro

import androidx.lifecycle.ViewModel
import se.gustavkarlsson.skylight.android.krate.SignalFirstRunCompleted
import se.gustavkarlsson.skylight.android.krate.SkylightStore

class IntroViewModel(
	private val store: SkylightStore
) : ViewModel() {
	fun signalFirstRunCompleted() = store.issue(SignalFirstRunCompleted)
}

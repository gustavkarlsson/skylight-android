package se.gustavkarlsson.skylight.android.gui.screens.googleplayservices

import android.arch.lifecycle.ViewModel
import se.gustavkarlsson.skylight.android.krate.SignalGooglePlayServicesInstalled
import se.gustavkarlsson.skylight.android.krate.SkylightStore

class GooglePlayServicesViewModel(
	private val store: SkylightStore
) : ViewModel() {
	fun signalGooglePlayServicesInstalled() = store.issue(SignalGooglePlayServicesInstalled)
}

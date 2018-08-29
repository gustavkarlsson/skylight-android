package se.gustavkarlsson.skylight.android.gui.screens.permission

import android.arch.lifecycle.ViewModel
import se.gustavkarlsson.skylight.android.krate.SignalLocationPermissionGranted
import se.gustavkarlsson.skylight.android.krate.SkylightStore

class PermissionViewModel(
	private val store: SkylightStore
) : ViewModel() {
	fun signalLocationPermissionGranted() = store.issue(SignalLocationPermissionGranted)
}

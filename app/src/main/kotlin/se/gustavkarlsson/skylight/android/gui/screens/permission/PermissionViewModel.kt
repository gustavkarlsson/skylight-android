package se.gustavkarlsson.skylight.android.gui.screens.permission

import androidx.lifecycle.ViewModel
import se.gustavkarlsson.skylight.android.krate.Command
import se.gustavkarlsson.skylight.android.krate.SkylightStore

class PermissionViewModel(
	private val store: SkylightStore
) : ViewModel() {
	fun signalLocationPermissionGranted() = store.issue(Command.SignalLocationPermissionGranted)
}

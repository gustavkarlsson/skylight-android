package se.gustavkarlsson.skylight.android.gui.screens.permission

import android.arch.lifecycle.ViewModel
import io.reactivex.Flowable
import io.reactivex.Maybe
import se.gustavkarlsson.skylight.android.krate.SignalLocationPermissionGranted
import se.gustavkarlsson.skylight.android.krate.SkylightStore

class PermissionViewModel(
	private val store: SkylightStore
) : ViewModel() {

	private val locationPermissionGranted = store.states
		.flatMapMaybe {
			val granted = it.isLocationPermissionGranted
			if (granted != null) {
				Maybe.just(granted)
			} else {
				Maybe.empty()
			}
		}

	val locationPermissionCheckboxChecked: Flowable<Boolean> = locationPermissionGranted

	val locationPermissionFixButtonVisibility: Flowable<Boolean> =
		locationPermissionCheckboxChecked.map(Boolean::not)

	fun signalLocationPermissionGranted() = store.issue(SignalLocationPermissionGranted)
}

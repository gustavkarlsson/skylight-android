package se.gustavkarlsson.skylight.android.gui.screens.setup

import android.arch.lifecycle.ViewModel
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.functions.BiFunction
import se.gustavkarlsson.skylight.android.krate.SignalLocationPermissionGranted
import se.gustavkarlsson.skylight.android.krate.SkylightStore

class SetupViewModel(
	private val store: SkylightStore
) : ViewModel() {

	private val googlePlayServicesAvailable = store.states
		.flatMapMaybe {
			val granted = it.isGooglePlayServicesAvailable
			if (granted != null) {
				Maybe.just(granted)
			} else {
				Maybe.empty()
			}
		}

	val googlePlayServicesCheckboxChecked: Flowable<Boolean> = googlePlayServicesAvailable

	val googlePlayServicesFixButtonVisibility: Flowable<Boolean> =
		googlePlayServicesCheckboxChecked.map(Boolean::not)

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

	val startButtonVisibility: Flowable<Boolean> = Flowable.combineLatest(
		googlePlayServicesCheckboxChecked,
		locationPermissionCheckboxChecked,
		BiFunction { google: Boolean, location: Boolean -> google && location }
	)

	fun signalLocationPermissionGranted() = store.issue(SignalLocationPermissionGranted)
}

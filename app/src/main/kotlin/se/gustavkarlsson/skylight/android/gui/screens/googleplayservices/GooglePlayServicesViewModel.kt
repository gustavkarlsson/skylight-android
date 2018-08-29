package se.gustavkarlsson.skylight.android.gui.screens.googleplayservices

import android.arch.lifecycle.ViewModel
import io.reactivex.Flowable
import io.reactivex.Maybe
import se.gustavkarlsson.skylight.android.krate.SignalGooglePlayServicesInstalled
import se.gustavkarlsson.skylight.android.krate.SkylightStore

class GooglePlayServicesViewModel(
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

	fun signalGooglePlayServicesInstalled() = store.issue(SignalGooglePlayServicesInstalled)
}

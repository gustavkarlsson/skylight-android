package se.gustavkarlsson.skylight.android.gui.screens.setup

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.jakewharton.rxrelay2.BehaviorRelay
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.functions.BiFunction

class SetupViewModel(application: Application) : AndroidViewModel(application) {

	val googlePlayServicesCheckboxChecked: Flowable<Boolean> =
		Single.fromCallable {
			GoogleApiAvailability.getInstance()
				.isGooglePlayServicesAvailable(getApplication()) == ConnectionResult.SUCCESS
		}.toFlowable()
	val googlePlayServicesFixButtonVisibility: Flowable<Boolean> =
		googlePlayServicesCheckboxChecked.map(Boolean::not)

	private val locationPermissionRelay = BehaviorRelay.create<Boolean>()
	fun setLocationPermission(granted: Boolean) = locationPermissionRelay.accept(granted)
	val locationPermissionCheckboxChecked: Flowable<Boolean> =
		locationPermissionRelay.toFlowable(BackpressureStrategy.LATEST)
	val locationPermissionFixButtonVisibility: Flowable<Boolean> =
		locationPermissionCheckboxChecked.map(Boolean::not)

	val startButtonVisibility: Flowable<Boolean> = Flowable.combineLatest(
		googlePlayServicesCheckboxChecked,
		locationPermissionCheckboxChecked,
		BiFunction { google: Boolean, location: Boolean -> google && location }
	)
}

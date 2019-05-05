package se.gustavkarlsson.skylight.android.services_impl

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.jakewharton.rxrelay2.BehaviorRelay
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import se.gustavkarlsson.skylight.android.services.PermissionChecker
import timber.log.Timber

class AndroidPermissionChecker(
	private val context: Context,
	private val locationPermission: String
) : PermissionChecker {

	private val isLocationGrantedRelay = BehaviorRelay.createDefault(isLocationGranted())

	private fun isLocationGranted(): Boolean {
		val result = ContextCompat.checkSelfPermission(context, locationPermission)
		val granted = result == PackageManager.PERMISSION_GRANTED
		Timber.d("$locationPermission granted=$granted")
		return granted
	}

	override val isLocationGranted: Flowable<Boolean> =
		isLocationGrantedRelay.toFlowable(BackpressureStrategy.LATEST)

	override fun signalPermissionGranted() {
		Timber.d("Signalled $locationPermission granted")
		isLocationGrantedRelay.accept(true)
	}
}

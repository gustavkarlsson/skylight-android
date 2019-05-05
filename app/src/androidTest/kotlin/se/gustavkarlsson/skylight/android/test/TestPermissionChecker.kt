package se.gustavkarlsson.skylight.android.test

import com.jakewharton.rxrelay2.BehaviorRelay
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import se.gustavkarlsson.skylight.android.services.PermissionChecker

class TestPermissionChecker(
	initialIsLocationGranted: Boolean
) : PermissionChecker {

	val isLocationGrantedRelay = BehaviorRelay.createDefault(initialIsLocationGranted)

	override val isLocationGranted: Flowable<Boolean> =
		isLocationGrantedRelay.toFlowable(BackpressureStrategy.LATEST)

	override fun signalPermissionGranted() = isLocationGrantedRelay.accept(true)

}

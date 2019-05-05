package se.gustavkarlsson.skylight.android.modules

import com.jakewharton.rxrelay2.BehaviorRelay
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import org.koin.dsl.module.module
import se.gustavkarlsson.skylight.android.services.PermissionChecker

val testPermissionsModule = module {

	single {
		TestPermissionChecker(true)
	}

	single<PermissionChecker>(override = true) {
		get<TestPermissionChecker>()
	}

}

class TestPermissionChecker(
	initialIsLocationGranted: Boolean
) : PermissionChecker {

	val isLocationGrantedRelay =
		BehaviorRelay.createDefault(initialIsLocationGranted)

	override val isLocationGranted: Flowable<Boolean> =
		isLocationGrantedRelay.toFlowable(BackpressureStrategy.LATEST)

	override fun signalPermissionGranted() = isLocationGrantedRelay.accept(true)

}

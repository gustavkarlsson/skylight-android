package se.gustavkarlsson.skylight.android.modules

import com.jakewharton.rxrelay2.BehaviorRelay
import io.reactivex.BackpressureStrategy
import io.reactivex.Observable
import org.koin.dsl.module.module
import se.gustavkarlsson.skylight.android.feature.main.PermissionChecker

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

	override val isLocationGranted: Observable<Boolean> =
		isLocationGrantedRelay.toObservable(BackpressureStrategy.LATEST)

	override fun signalPermissionGranted() = isLocationGrantedRelay.accept(true)

}

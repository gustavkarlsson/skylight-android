package se.gustavkarlsson.skylight.android.modules

import com.jakewharton.rxrelay2.BehaviorRelay
import io.reactivex.BackpressureStrategy
import io.reactivex.Observable
import org.koin.dsl.module.module
import se.gustavkarlsson.skylight.android.feature.googleplayservices.GooglePlayServicesChecker

val testGooglePlayServicesModule = module {

	single {
		TestGooglePlayServicesChecker(true)
	}

	single<GooglePlayServicesChecker>(override = true) {
		get<TestGooglePlayServicesChecker>()
	}

}

class TestGooglePlayServicesChecker(initialIsAvailable: Boolean) :
	GooglePlayServicesChecker {

	val isAvailableRelay = BehaviorRelay.createDefault(initialIsAvailable)

	override val isAvailable: Observable<Boolean> = isAvailableRelay.toObservable(BackpressureStrategy.LATEST)

	override fun signalInstalled() {
		isAvailableRelay.accept(true)
	}

}

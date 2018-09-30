package se.gustavkarlsson.skylight.android.modules

import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork
import com.github.pwittchen.reactivenetwork.library.rx2.internet.observing.InternetObservingSettings
import io.reactivex.BackpressureStrategy
import org.koin.dsl.module.module

val connectivityModule = module {

	single("connectivity") {
		val settings = InternetObservingSettings
			.interval(10_000)
			.timeout(10_000)
			.build()
		ReactiveNetwork.observeInternetConnectivity(settings)
			.toFlowable(BackpressureStrategy.LATEST)
	}

}

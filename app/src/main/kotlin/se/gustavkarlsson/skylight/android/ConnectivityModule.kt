package se.gustavkarlsson.skylight.android

import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork
import io.reactivex.BackpressureStrategy
import org.koin.dsl.module.module

val connectivityModule = module {

	single("connectivity") {
		ReactiveNetwork.observeInternetConnectivity()
			.toFlowable(BackpressureStrategy.LATEST)
	}

}

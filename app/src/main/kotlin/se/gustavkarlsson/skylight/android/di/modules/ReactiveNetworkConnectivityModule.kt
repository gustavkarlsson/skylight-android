package se.gustavkarlsson.skylight.android.di.modules

import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable

class ReactiveNetworkConnectivityModule : ConnectivityModule {
	override val connectivityFlowable: Flowable<Boolean> by lazy {
		ReactiveNetwork.observeInternetConnectivity()
			.toFlowable(BackpressureStrategy.LATEST)
	}
}

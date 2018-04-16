package se.gustavkarlsson.skylight.android.di.modules

import io.reactivex.Flowable

interface ConnectivityModule {
	val connectivityFlowable: Flowable<Boolean>
}

package se.gustavkarlsson.skylight.android.extensions

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.LiveDataReactiveStreams
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Observable

fun <T> Observable<T>.toLiveData(backpressureStrategy: BackpressureStrategy) : LiveData<T> {
	return toFlowable(backpressureStrategy).toLiveData()
}

fun <T> Flowable<T>.toLiveData() : LiveData<T> {
	return LiveDataReactiveStreams.fromPublisher(this)
}

package se.gustavkarlsson.skylight.android.services_impl.streams

import io.reactivex.Observable
import se.gustavkarlsson.skylight.android.services.streams.Stream
import se.gustavkarlsson.skylight.android.services.streams.StreamSubscription

class RxStream<T>(private val observable: Observable<T>) : Stream<T> {
	override fun subscribe(action: (T) -> Unit): StreamSubscription {
		val disposable = observable.subscribe { action(it) }
		return RxStreamSubscription(disposable)
	}
}

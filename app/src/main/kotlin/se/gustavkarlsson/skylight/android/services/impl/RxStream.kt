package se.gustavkarlsson.skylight.android.services.impl

import io.reactivex.Observable
import se.gustavkarlsson.skylight.android.services.Stream
import se.gustavkarlsson.skylight.android.services.StreamSubscription

class RxStream<T>(private val observable: Observable<T>) : Stream<T> {
	override fun subscribe(action: (T) -> Unit): StreamSubscription {
		val disposable = observable.subscribe { action(it) }
		return RxStreamSubscription(disposable)
	}
}

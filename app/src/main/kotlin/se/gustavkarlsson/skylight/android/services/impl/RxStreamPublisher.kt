package se.gustavkarlsson.skylight.android.services.impl

import io.reactivex.Observer
import se.gustavkarlsson.skylight.android.services.StreamPublisher

class RxStreamPublisher<T>(private val observer: Observer<T>) : StreamPublisher<T> {
	override fun publish(value: T) {
		observer.onNext(value)
	}
}

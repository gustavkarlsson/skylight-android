package se.gustavkarlsson.skylight.android.services_impl.streams

import io.reactivex.Observer
import se.gustavkarlsson.skylight.android.services.streams.StreamPublisher

class RxStreamPublisher<T>(private val observer: Observer<T>) : StreamPublisher<T> {
	override fun publish(value: T) {
		observer.onNext(value)
	}
}

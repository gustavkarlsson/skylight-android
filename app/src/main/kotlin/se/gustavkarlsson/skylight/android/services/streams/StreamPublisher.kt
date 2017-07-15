package se.gustavkarlsson.skylight.android.services.streams

interface StreamPublisher<T> {
	fun publish(value: T)
}

package se.gustavkarlsson.skylight.android.services

interface StreamPublisher<T> {
	fun publish(value: T)
}

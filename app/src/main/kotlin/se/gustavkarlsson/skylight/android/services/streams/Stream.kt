package se.gustavkarlsson.skylight.android.services.streams

interface Stream<T> {
	fun subscribe(action: (T) -> Unit): StreamSubscription
}

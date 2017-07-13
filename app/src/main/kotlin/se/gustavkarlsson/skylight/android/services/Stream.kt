package se.gustavkarlsson.skylight.android.services

interface Stream<T> {
	fun subscribe(action: (T) -> Unit): StreamSubscription
}

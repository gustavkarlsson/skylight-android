package se.gustavkarlsson.skylight.android.services

interface Provider<T> {
	fun get(): T
}

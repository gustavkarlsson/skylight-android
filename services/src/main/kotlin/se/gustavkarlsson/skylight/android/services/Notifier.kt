package se.gustavkarlsson.skylight.android.services

interface Notifier<T> {
	fun notify(value: T)
}

package se.gustavkarlsson.skylight.android.services

interface Presenter<T> {
	fun present(value: T)
}

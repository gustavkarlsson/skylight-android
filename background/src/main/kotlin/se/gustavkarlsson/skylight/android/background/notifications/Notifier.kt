package se.gustavkarlsson.skylight.android.background.notifications

interface Notifier<in T> {
	fun notify(value: T)
}

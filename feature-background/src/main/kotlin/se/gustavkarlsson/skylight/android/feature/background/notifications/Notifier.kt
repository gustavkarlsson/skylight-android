package se.gustavkarlsson.skylight.android.feature.background.notifications

interface Notifier<in T> {
	fun notify(value: T)
}

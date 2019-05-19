package se.gustavkarlsson.skylight.android.feature.background.scheduling

internal interface Scheduler {
	fun schedule()
	fun unschedule()
}

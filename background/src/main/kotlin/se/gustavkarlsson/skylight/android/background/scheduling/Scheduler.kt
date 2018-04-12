package se.gustavkarlsson.skylight.android.background.scheduling

internal interface Scheduler {
	fun schedule()
	fun unschedule()
}

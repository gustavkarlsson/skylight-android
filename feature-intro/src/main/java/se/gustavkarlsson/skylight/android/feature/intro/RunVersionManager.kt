package se.gustavkarlsson.skylight.android.feature.intro

internal interface RunVersionManager {
	val isFirstRun: Boolean
	fun signalRunCompleted()
}

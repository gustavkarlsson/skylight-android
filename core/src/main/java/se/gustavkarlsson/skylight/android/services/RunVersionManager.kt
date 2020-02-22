package se.gustavkarlsson.skylight.android.services

interface RunVersionManager {
    val isFirstRun: Boolean
    fun signalRunCompleted()
}

package se.gustavkarlsson.skylight.android.lib.runversion

interface RunVersionManager {
    val isFirstRun: Boolean
    fun signalRunCompleted()
}

package se.gustavkarlsson.skylight.android.lib.runversion

interface RunVersionManager {
    suspend fun isFirstRun(): Boolean
    suspend fun signalRunCompleted()
}

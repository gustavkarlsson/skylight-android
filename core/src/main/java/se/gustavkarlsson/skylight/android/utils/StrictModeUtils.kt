package se.gustavkarlsson.skylight.android.utils

import android.os.StrictMode

// TODO Identify all usages of allowThreadDiskReadsInStrictMode and remove them
@Deprecated("Using this indicates a bug where the main thread is allowed to read from disk")
fun <T> allowThreadDiskReadsInStrictMode(block: () -> T): T = allow(StrictMode::allowThreadDiskReads, block)

// TODO Identify all usages of allowThreadDiskWritesInStrictMode and remove them
@Deprecated("Using this indicates a bug where the main thread is allowed to write to disk")
fun <T> allowThreadDiskWritesInStrictMode(block: () -> T): T = allow(StrictMode::allowThreadDiskWrites, block)

@Synchronized
private fun <T> allow(allowCall: () -> StrictMode.ThreadPolicy, block: () -> T): T {
    val oldThreadPolicy = allowCall()
    return try {
        block()
    } finally {
        StrictMode.setThreadPolicy(oldThreadPolicy)
    }
}

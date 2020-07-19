package se.gustavkarlsson.skylight.android.core.utils

import android.os.StrictMode

fun <T> allowDiskReadsInStrictMode(block: () -> T): T = allow(StrictMode::allowThreadDiskReads, block)

fun <T> allowDiskWritesInStrictMode(block: () -> T): T = allow(StrictMode::allowThreadDiskWrites, block)

fun <T> allowDiskReadsAndWritesInStrictMode(block: () -> T): T {
    val allow = {
        val oldThreadPolicy = StrictMode.allowThreadDiskReads()
        StrictMode.allowThreadDiskWrites()
        oldThreadPolicy
    }
    return allow(allow, block)
}

@Synchronized
private fun <T> allow(allow: () -> StrictMode.ThreadPolicy, block: () -> T): T {
    val oldThreadPolicy = allow()
    return try {
        block()
    } finally {
        StrictMode.setThreadPolicy(oldThreadPolicy)
    }
}

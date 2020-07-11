package se.gustavkarlsson.skylight.android

import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import timber.log.Timber

internal fun initLogging() {
    if (BuildConfig.DEBUG) {
        Timber.plant(Timber.DebugTree())
    }
    Timber.plant(CrashlyticsTree(FirebaseCrashlytics.getInstance()))
}

private class CrashlyticsTree(private val crashlytics: FirebaseCrashlytics) : Timber.Tree() {
    override fun log(priority: Int, tag: String?, message: String, throwable: Throwable?) {
        if (priority == Log.VERBOSE || priority == Log.DEBUG) {
            return
        }
        val crashlyticsMessage = buildMessage(priority, tag, message)
        crashlytics.log(crashlyticsMessage)
        if (priority == Log.ERROR) {
            val exception = throwable ?: NoException(message)
            crashlytics.recordException(exception)
        }
    }
}

private fun buildMessage(
    priority: Int,
    tag: String?,
    message: String
) = buildString {
    priorityNames[priority]?.let {
        append("$it ")
    }
    tag?.let {
        append("($it) ")
    }
    append(message)
}

private val priorityNames: Map<Int, String> = mapOf(
    Log.ASSERT to "ASSERT",
    Log.DEBUG to "DEBUG",
    Log.ERROR to "ERROR",
    Log.INFO to "INFO",
    Log.VERBOSE to "VERBOSE",
    Log.WARN to "WARN"
)

private class NoException(message: String) : Exception(message)

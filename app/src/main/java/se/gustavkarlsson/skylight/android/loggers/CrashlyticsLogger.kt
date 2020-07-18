package se.gustavkarlsson.skylight.android.loggers

import com.google.firebase.crashlytics.FirebaseCrashlytics
import se.gustavkarlsson.skylight.android.logging.Logger

internal class CrashlyticsLogger(private val crashlytics: FirebaseCrashlytics) : Logger {
    override fun isEnabledFor(level: Logger.Level) = level >= Logger.Level.INFO

    override fun log(level: Logger.Level, tag: String, throwable: Throwable?, message: String?) {
        if (message != null) {
            val crashlyticsMessage = "$level ($tag): $message"
            crashlytics.log(crashlyticsMessage)
        }
        if (level == Logger.Level.ERROR) {
            val exception = when {
                throwable != null -> throwable
                message != null -> NoException(message)
                else -> NoException()
            }
            crashlytics.recordException(exception)
        }
    }
}

private class NoException : Exception {
    constructor() : super()
    constructor(message: String) : super(message)
}

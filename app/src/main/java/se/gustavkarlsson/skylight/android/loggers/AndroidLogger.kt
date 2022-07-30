package se.gustavkarlsson.skylight.android.loggers

import android.app.Application
import logcat.AndroidLogcatLogger
import logcat.LogPriority
import logcat.asLog
import logcat.logcat
import se.gustavkarlsson.skylight.android.core.logging.Logger

internal object AndroidLogger : Logger {

    fun initialize(application: Application) {
        AndroidLogcatLogger.installOnDebuggableApp(application)
    }

    override fun isEnabledFor(level: Logger.Level) = true

    override fun log(level: Logger.Level, tag: String, throwable: Throwable?, message: String?) {
        logcat(tag, level.toPriority()) {
            val completeMessage = buildMessage(message, throwable)
            if (completeMessage.isBlank()) return
            completeMessage
        }
    }
}

private fun Logger.Level.toPriority() = when (this) {
    Logger.Level.VERBOSE -> LogPriority.VERBOSE
    Logger.Level.DEBUG -> LogPriority.DEBUG
    Logger.Level.INFO -> LogPriority.INFO
    Logger.Level.WARN -> LogPriority.WARN
    Logger.Level.ERROR -> LogPriority.ERROR
}

private fun buildMessage(message: String?, throwable: Throwable?) = buildString {
    if (!message.isNullOrBlank()) {
        append(message)
        if (throwable != null) {
            append('\n')
        }
    }
    if (throwable != null) {
        append(throwable.asLog())
    }
}

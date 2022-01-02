package se.gustavkarlsson.skylight.android.loggers

import android.os.Build
import android.util.Log
import se.gustavkarlsson.skylight.android.core.logging.Logger
import java.io.PrintWriter
import java.io.StringWriter

internal object AndroidLogger : Logger {
    override fun isEnabledFor(level: Logger.Level) = true

    override fun log(level: Logger.Level, tag: String, throwable: Throwable?, message: String?) {
        if (throwable == null && message.isNullOrBlank()) return
        val actualMessage = when {
            throwable != null && !message.isNullOrBlank() -> buildMessage(message, throwable)
            throwable != null -> buildMessage(throwable)
            !message.isNullOrBlank() -> message
            else -> return
        }
        val priority = level.toPriority()
        val actualTag = limitTagLength(tag)
        performLog(priority, actualTag, actualMessage)
    }
}

private fun buildMessage(message: String, throwable: Throwable): String {
    return message + '\n' + buildMessage(throwable)
}

// Stolen from Timber
private fun buildMessage(throwable: Throwable): String {
    // Don't replace this with Log.getStackTraceString(). It hides UnknownHostException, which is not what we want.
    val stringWriter = StringWriter(256)
    val printWriter = PrintWriter(stringWriter, false)
    throwable.printStackTrace(printWriter)
    printWriter.flush()
    return stringWriter.toString()
}

private fun Logger.Level.toPriority() = when (this) {
    Logger.Level.VERBOSE -> Log.VERBOSE
    Logger.Level.DEBUG -> Log.DEBUG
    Logger.Level.INFO -> Log.INFO
    Logger.Level.WARN -> Log.WARN
    Logger.Level.ERROR -> Log.ERROR
}

private const val MAX_TAG_LENGTH = 23
private fun limitTagLength(tag: String) =
    if (tag.length <= MAX_TAG_LENGTH) {
        tag
    } else {
        tag.substring(0, MAX_TAG_LENGTH)
    }

// Stolen from Timber DebugTree
private const val MAX_LOG_LENGTH = 4000
private fun performLog(priority: Int, tag: String, message: String) {
    val length = message.length
    if (message.length < MAX_LOG_LENGTH) {
        Log.println(priority, tag, message)
        return
    }
    var i = 0
    while (i < length) {
        var newline = message.indexOf('\n', i)
        newline = if (newline != -1) newline else length
        do {
            val end = newline.coerceAtMost(i + MAX_LOG_LENGTH)
            val part = message.substring(i, end)
            Log.println(priority, tag, part)
            i = end
        } while (i < newline)
        i++
    }
}

package se.gustavkarlsson.skylight.android.logging

import java.util.regex.Pattern

private val loggers = mutableListOf<Logger>()

fun addLogger(logger: Logger) = loggers.add(logger)

fun logVerbose(throwable: Throwable? = null, message: (() -> String)? = null) {
    log(Logger.Level.VERBOSE, throwable, message)
}

fun logDebug(throwable: Throwable? = null, message: (() -> String)? = null) {
    log(Logger.Level.DEBUG, throwable, message)
}

fun logInfo(throwable: Throwable? = null, message: (() -> String)? = null) {
    log(Logger.Level.INFO, throwable, message)
}

fun logWarn(throwable: Throwable? = null, message: (() -> String)? = null) {
    log(Logger.Level.WARN, throwable, message)
}

fun logError(throwable: Throwable? = null, message: (() -> String)? = null) {
    log(Logger.Level.ERROR, throwable, message)
}

private fun log(level: Logger.Level, throwable: Throwable?, messageBlock: (() -> String)? = null) {
    var lazyTag: String? = null // Don't use kotlin lazy, as we don't know the number of stack entries
    val message by lazy { messageBlock?.invoke() }
    for (logger in loggers) {
        if (logger.isEnabledFor(level)) {
            val tag = lazyTag ?: createTag().also { lazyTag = it }
            logger.log(level, tag, throwable, message)
        }
    }
}

private fun createTag() =
    Throwable().stackTrace
        .drop(4)
        .first()
        .let(::createStackElementTag)

private fun createStackElementTag(element: StackTraceElement): String {
    var tag = element.className.substringAfterLast('.')
    val matcher = ANONYMOUS_CLASS.matcher(tag)
    if (matcher.find()) {
        tag = matcher.replaceAll("")
    }
    return tag
}

private val ANONYMOUS_CLASS = Pattern.compile("(\\$\\d+)+$")

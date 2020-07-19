package se.gustavkarlsson.skylight.android.core.logging

interface Logger {
    fun isEnabledFor(level: Level): Boolean
    fun log(level: Level, tag: String, throwable: Throwable?, message: String?)
    enum class Level { VERBOSE, DEBUG, INFO, WARN, ERROR }
}

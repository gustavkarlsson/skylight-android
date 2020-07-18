package se.gustavkarlsson.skylight.android.logging

import android.os.Build
import android.util.Log
import timber.log.Timber

object TimberLogger : Logger {
    override fun isEnabledFor(level: Logger.Level) = true

    override fun log(level: Logger.Level, tag: String, throwable: Throwable?, message: String?) {
        val priority = when (level) {
            Logger.Level.VERBOSE -> Log.VERBOSE
            Logger.Level.DEBUG -> Log.DEBUG
            Logger.Level.INFO -> Log.INFO
            Logger.Level.WARN -> Log.WARN
            Logger.Level.ERROR -> Log.ERROR
        }
        val actualTag = limitTagLength(tag)
        Timber.tag(actualTag)
        Timber.log(priority, throwable, message)
    }

    private fun limitTagLength(tag: String) =
        if (tag.length <= MAX_TAG_LENGTH || Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            tag
        } else {
            tag.substring(0, MAX_TAG_LENGTH)
        }

    private const val MAX_TAG_LENGTH = 23
}

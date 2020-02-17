package se.gustavkarlsson.skylight.android

import org.koin.log.Logger
import timber.log.Timber

internal object KoinTimberLogger : Logger {
    override fun debug(msg: String) = Timber.d(msg)
    override fun err(msg: String) = Timber.e(msg)
    override fun info(msg: String) = Timber.d(msg)
}

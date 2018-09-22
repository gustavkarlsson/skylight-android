package se.gustavkarlsson.skylight.android.util

import org.koin.log.Logger
import timber.log.Timber

class KoinTimberLogger : Logger {
	override fun debug(msg: String) = Timber.d(msg)
	override fun err(msg: String) = Timber.e(msg)
	override fun info(msg: String) = Timber.d(msg)
}

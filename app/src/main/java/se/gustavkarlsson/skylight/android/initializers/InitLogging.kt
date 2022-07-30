package se.gustavkarlsson.skylight.android.initializers

import android.app.Application
import com.google.firebase.crashlytics.FirebaseCrashlytics
import se.gustavkarlsson.skylight.android.BuildConfig
import se.gustavkarlsson.skylight.android.core.logging.addLogger
import se.gustavkarlsson.skylight.android.loggers.AndroidLogger
import se.gustavkarlsson.skylight.android.loggers.CrashlyticsLogger

internal fun Application.initLogging() {
    val logger = if (BuildConfig.DEBUG) {
        AndroidLogger.also { it.initialize(this) }
    } else {
        CrashlyticsLogger(FirebaseCrashlytics.getInstance())
    }
    addLogger(logger)
}

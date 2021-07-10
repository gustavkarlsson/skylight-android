package se.gustavkarlsson.skylight.android.initializers

import com.google.firebase.crashlytics.FirebaseCrashlytics
import se.gustavkarlsson.skylight.android.BuildConfig
import se.gustavkarlsson.skylight.android.core.logging.addLogger
import se.gustavkarlsson.skylight.android.loggers.AndroidLogger
import se.gustavkarlsson.skylight.android.loggers.CrashlyticsLogger

internal fun initLogging() {
    val logger = if (BuildConfig.DEBUG) {
        AndroidLogger
    } else {
        CrashlyticsLogger(FirebaseCrashlytics.getInstance())
    }
    addLogger(logger)
}

package se.gustavkarlsson.skylight.android.initializers

import com.google.firebase.crashlytics.FirebaseCrashlytics
import se.gustavkarlsson.skylight.android.BuildConfig
import se.gustavkarlsson.skylight.android.loggers.CrashlyticsLogger
import se.gustavkarlsson.skylight.android.loggers.TimberLogger
import se.gustavkarlsson.skylight.android.logging.addLogger

internal fun initLogging() {
    val logger = if (BuildConfig.DEBUG) {
        TimberLogger
    } else {
        CrashlyticsLogger(FirebaseCrashlytics.getInstance())
    }
    addLogger(logger)
}

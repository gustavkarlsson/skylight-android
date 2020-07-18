package se.gustavkarlsson.skylight.android.initializers

import io.reactivex.plugins.RxJavaPlugins
import se.gustavkarlsson.skylight.android.logging.logError

internal fun initRxJavaErrorHandling() {
    RxJavaPlugins.setErrorHandler {
        logError(it) { "Unhandled RxJava error" }
    }
}

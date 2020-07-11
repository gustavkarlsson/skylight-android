package se.gustavkarlsson.skylight.android

import io.reactivex.plugins.RxJavaPlugins
import timber.log.Timber

internal fun initRxJavaErrorHandling() {
    RxJavaPlugins.setErrorHandler {
        Timber.e(it, "Unhandled RxJava error")
    }
}

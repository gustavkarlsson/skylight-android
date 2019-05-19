package se.gustavkarlsson.skylight.android.util

import android.util.Log
import com.crashlytics.android.core.CrashlyticsCore
import timber.log.Timber

class CrashlyticsTree(private val core: CrashlyticsCore) : Timber.Tree() {
    override fun log(priority: Int, tag: String?, message: String, throwable: Throwable?) {
        if (priority == Log.VERBOSE || priority == Log.DEBUG) {
            return
        }

		core.log(priority, tag.toString(), message)

		if (priority == Log.ERROR) {
			val exception = throwable ?: NoException(message)
			core.logException(exception)
		}
    }

	private class NoException(message: String) : Exception(message)
}

package se.gustavkarlsson.skylight.android.lib.runversion

import android.content.Context
import android.content.Context.MODE_PRIVATE
import dagger.Reusable
import se.gustavkarlsson.skylight.android.core.VersionCode
import se.gustavkarlsson.skylight.android.core.logging.logDebug
import javax.inject.Inject

// TODO Use coroutines
@Reusable
internal class SharedPreferencesRunVersionManager @Inject constructor(
    context: Context,
    @VersionCode private val currentVersionCode: Int,
) : RunVersionManager {

    private val prefs by lazy {
        context.getSharedPreferences(PREFS_FILE_NAME, MODE_PRIVATE)
    }

    override val isFirstRun: Boolean
        get() {
            val lastRunVersion = prefs.getInt(LAST_RUN_VERSION_KEY, NO_VERSION)
            logDebug { "Last run version: $lastRunVersion" }
            return lastRunVersion == NO_VERSION
        }

    override fun signalRunCompleted() {
        prefs.edit().putInt(LAST_RUN_VERSION_KEY, currentVersionCode).apply()
        logDebug { "Signalled run completed" }
    }

    companion object {
        private const val PREFS_FILE_NAME = "run_version"
        private const val LAST_RUN_VERSION_KEY = "last_run_version"
        private const val NO_VERSION = -1
    }
}

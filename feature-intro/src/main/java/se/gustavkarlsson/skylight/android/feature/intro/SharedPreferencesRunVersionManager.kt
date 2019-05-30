package se.gustavkarlsson.skylight.android.feature.intro

import android.content.Context
import android.content.Context.MODE_PRIVATE
import timber.log.Timber

internal class SharedPreferencesRunVersionManager(
	context: Context,
	private val currentVersionCode: Int
) : RunVersionManager {

	private val prefs = context.getSharedPreferences(PREFS_FILE_NAME, MODE_PRIVATE)

	override val isFirstRun: Boolean
		get() {
			val lastRunVersion = prefs.getInt(LAST_RUN_VERSION_KEY, NO_VERSION)
			Timber.d("Last run version: $lastRunVersion")
			return lastRunVersion == NO_VERSION
		}

	override fun signalRunCompleted() {
		prefs.edit().putInt(LAST_RUN_VERSION_KEY, currentVersionCode).apply()
		Timber.d("Signalled run completed")
	}

	companion object {
		private const val PREFS_FILE_NAME = "run_version"
		private const val LAST_RUN_VERSION_KEY = "last_run_version"
		private const val NO_VERSION = -1
	}
}

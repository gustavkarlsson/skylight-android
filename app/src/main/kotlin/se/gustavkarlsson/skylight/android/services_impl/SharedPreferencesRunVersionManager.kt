package se.gustavkarlsson.skylight.android.services_impl

import android.content.Context
import android.content.Context.MODE_PRIVATE
import se.gustavkarlsson.skylight.android.BuildConfig
import se.gustavkarlsson.skylight.android.services.RunVersionManager


class SharedPreferencesRunVersionManager(
	context: Context,
	private val currentVersionCode: Int = BuildConfig.VERSION_CODE
) : RunVersionManager {

	private val prefs = context.getSharedPreferences(PREFS_FILE_NAME, MODE_PRIVATE)

	override val isFirstRun: Boolean
		get() = getLastRunVersion() == NO_VERSION

	override fun signalFirstRunCompleted() {
		prefs.edit().putInt(LAST_RUN_VERSION_KEY, currentVersionCode).apply()
	}

	private fun getLastRunVersion() = prefs.getInt(LAST_RUN_VERSION_KEY, NO_VERSION)

	companion object {
		private const val PREFS_FILE_NAME = "run_version"
		private const val LAST_RUN_VERSION_KEY = "last_run_version"
		private const val NO_VERSION = -1
	}
}

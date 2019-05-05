package se.gustavkarlsson.skylight.android.services_impl

import android.content.Context
import android.content.Context.MODE_PRIVATE
import com.jakewharton.rxrelay2.BehaviorRelay
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import se.gustavkarlsson.skylight.android.BuildConfig
import se.gustavkarlsson.skylight.android.services.RunVersionManager
import timber.log.Timber

class SharedPreferencesRunVersionManager(
	context: Context,
	private val currentVersionCode: Int = BuildConfig.VERSION_CODE
) : RunVersionManager {

	private val prefs = context.getSharedPreferences(PREFS_FILE_NAME, MODE_PRIVATE)

	private val isFirstRunRelay = BehaviorRelay.createDefault(isFirstRun())

	private fun isFirstRun(): Boolean {
		val lastRunVersion = prefs.getInt(LAST_RUN_VERSION_KEY, NO_VERSION)
		Timber.d("Last run version: $lastRunVersion")
		return lastRunVersion == NO_VERSION
	}

	override val isFirstRun: Flowable<Boolean> =
		isFirstRunRelay.toFlowable(BackpressureStrategy.LATEST)

	override fun signalFirstRunCompleted() {
		prefs.edit().putInt(LAST_RUN_VERSION_KEY, currentVersionCode).apply()
		Timber.d("Signalled first run completed")
		isFirstRunRelay.accept(false)
	}

	companion object {
		private const val PREFS_FILE_NAME = "run_version"
		private const val LAST_RUN_VERSION_KEY = "last_run_version"
		private const val NO_VERSION = -1
	}
}

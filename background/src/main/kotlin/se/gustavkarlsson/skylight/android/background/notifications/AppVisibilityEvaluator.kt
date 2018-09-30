package se.gustavkarlsson.skylight.android.background.notifications

import android.app.ActivityManager
import android.app.ActivityManager.RunningAppProcessInfo
import android.app.KeyguardManager

internal class AppVisibilityEvaluator(
	private val keyguardManager: KeyguardManager
) {

	fun isVisible(): Boolean {
		val processInfo = getProcessInfo()
		if (!isInForeground(processInfo)) return false

		// app is in foreground, but screen can be locked
		return !keyguardManager.isKeyguardLocked
	}

	private fun getProcessInfo(): RunningAppProcessInfo {
		val processInfo = RunningAppProcessInfo()
		ActivityManager.getMyMemoryState(processInfo)
		return processInfo
	}

	private fun isInForeground(processInfo: RunningAppProcessInfo) =
		processInfo.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND
}

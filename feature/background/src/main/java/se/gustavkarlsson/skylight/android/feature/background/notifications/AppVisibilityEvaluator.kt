package se.gustavkarlsson.skylight.android.feature.background.notifications

import android.app.ActivityManager
import android.app.ActivityManager.RunningAppProcessInfo
import android.app.KeyguardManager

internal class AppVisibilityEvaluator(
    private val keyguardManager: KeyguardManager
) {

    fun isVisible(): Boolean {
        if (!isInForeground()) return false

        // app is in foreground, but screen can be locked
        return !keyguardManager.isKeyguardLocked
    }

    private fun getProcessInfo(): RunningAppProcessInfo {
        val processInfo = RunningAppProcessInfo()
        ActivityManager.getMyMemoryState(processInfo)
        return processInfo
    }

    private fun isInForeground() =
        getProcessInfo().importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND
}

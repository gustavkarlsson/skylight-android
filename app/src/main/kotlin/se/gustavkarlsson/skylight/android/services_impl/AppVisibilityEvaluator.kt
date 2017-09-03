package se.gustavkarlsson.skylight.android.services_impl

import android.app.ActivityManager
import android.app.ActivityManager.RunningAppProcessInfo
import android.app.KeyguardManager
import dagger.Reusable
import javax.inject.Inject

@Reusable
class AppVisibilityEvaluator
@Inject
constructor(
        private val keyguardManager: KeyguardManager
) {

    fun isVisible(): Boolean {
        val processInfo = getProcessInfo()
        if (!isInForeground(processInfo)) return false

        // app is in foreground, but screen can be locked
        return !keyguardManager.inKeyguardRestrictedInputMode()
    }

    private fun getProcessInfo(): RunningAppProcessInfo {
        val processInfo = RunningAppProcessInfo()
        ActivityManager.getMyMemoryState(processInfo)
        return processInfo
    }

    private fun isInForeground(processInfo: RunningAppProcessInfo) = processInfo.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND
}

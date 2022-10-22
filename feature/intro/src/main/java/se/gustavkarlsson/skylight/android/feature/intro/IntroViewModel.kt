package se.gustavkarlsson.skylight.android.feature.intro

import kotlinx.coroutines.launch
import se.gustavkarlsson.skylight.android.lib.runversion.RunVersionManager
import se.gustavkarlsson.skylight.android.lib.ui.CoroutineScopedService
import javax.inject.Inject

internal class IntroViewModel @Inject constructor(
    private val versionManager: RunVersionManager,
) : CoroutineScopedService() {
    fun registerScreenSeen() {
        scope.launch {
            versionManager.signalRunCompleted()
        }
    }
}

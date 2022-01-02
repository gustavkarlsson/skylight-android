package se.gustavkarlsson.skylight.android.feature.intro

import se.gustavkarlsson.skylight.android.lib.runversion.RunVersionManager
import se.gustavkarlsson.skylight.android.lib.scopedservice.ScopedService
import javax.inject.Inject

internal class IntroViewModel @Inject constructor(
    private val versionManager: RunVersionManager,
) : ScopedService {
    fun registerScreenSeen() = versionManager.signalRunCompleted()
}

package se.gustavkarlsson.skylight.android.feature.intro

import javax.inject.Inject
import se.gustavkarlsson.skylight.android.lib.runversion.RunVersionManager
import se.gustavkarlsson.skylight.android.lib.scopedservice.ScopedService

internal class IntroViewModel @Inject constructor(
    private val versionManager: RunVersionManager
) : ScopedService {
    fun registerScreenSeen() = versionManager.signalRunCompleted()
}

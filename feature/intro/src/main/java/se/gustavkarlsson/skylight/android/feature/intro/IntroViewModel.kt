package se.gustavkarlsson.skylight.android.feature.intro

import com.ioki.textref.TextRef
import javax.inject.Inject
import se.gustavkarlsson.skylight.android.lib.runversion.RunVersionManager
import se.gustavkarlsson.skylight.android.lib.scopedservice.ScopedService

internal class IntroViewModel @Inject constructor(
    private val versionManager: RunVersionManager
) : ScopedService {
    val privacyPolicyHtml: TextRef =
        TextRef(R.string.html_privacy_policy_link, TextRef(R.string.privacy_policy))

    fun registerScreenSeen() = versionManager.signalRunCompleted()
}

package se.gustavkarlsson.skylight.android.feature.intro

import com.ioki.textref.TextRef
import se.gustavkarlsson.skylight.android.ScopedService
import se.gustavkarlsson.skylight.android.services.RunVersionManager

internal class IntroViewModel(
    private val versionManager: RunVersionManager
) : ScopedService {
    val privacyPolicyHtml: TextRef =
        TextRef(R.string.html_privacy_policy_link, TextRef(R.string.privacy_policy))

    fun registerScreenSeen() = versionManager.signalRunCompleted()
}

package se.gustavkarlsson.skylight.android.lib.navigationsetup

import androidx.fragment.app.FragmentManager
import se.gustavkarlsson.skylight.android.core.logging.logInfo
import se.gustavkarlsson.skylight.android.lib.navigation.BackButtonHandler
import se.gustavkarlsson.skylight.android.lib.navigation.Navigator

internal class NavigatorBackButtonController(
    private val navigator: Navigator,
    private val fragmentManager: FragmentManager
) : BackButtonController {

    override fun onBackPressed() {
        val topFragment = fragmentManager.fragments.lastOrNull()
        val consumed = (topFragment as? BackButtonHandler)?.onBackPressed()
        when (consumed) {
            null -> logInfo { "Top fragment could not handle back press" }
            true -> logInfo { "Top fragment handled back press" }
            false -> logInfo { "Top fragment did not handle back press" }
        }
        if (consumed != true) navigator.closeScreen()
    }
}

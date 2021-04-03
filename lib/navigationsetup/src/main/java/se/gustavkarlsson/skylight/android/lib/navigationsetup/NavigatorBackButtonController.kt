package se.gustavkarlsson.skylight.android.lib.navigationsetup

import androidx.appcompat.app.AppCompatActivity
import se.gustavkarlsson.skylight.android.core.logging.logInfo
import se.gustavkarlsson.skylight.android.lib.navigation.BackPress
import se.gustavkarlsson.skylight.android.lib.navigation.Navigator

internal class NavigatorBackButtonController(
    private val navigator: Navigator,
    private val activity: AppCompatActivity,
) : BackButtonController {

    override fun onBackPress() {
        val topScreen = navigator.backstack.value?.lastOrNull()
        val backPress = topScreen?.run {
            activity.onBackPress()
        }
        when (backPress) {
            null -> logInfo { "Top screen could not handle back press" }
            BackPress.HANDLED -> logInfo { "Top screen handled back press" }
            BackPress.NOT_HANDLED -> logInfo { "Top screen did not handle back press" }
        }
        if (backPress != BackPress.HANDLED) navigator.closeScreen()
    }
}

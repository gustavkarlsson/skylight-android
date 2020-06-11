package se.gustavkarlsson.skylight.android.lib.navigationsetup

import androidx.fragment.app.FragmentManager
import se.gustavkarlsson.skylight.android.navigation.BackButtonHandler
import se.gustavkarlsson.skylight.android.navigation.Navigator
import timber.log.Timber

internal class NavigatorBackButtonController(
    private val navigator: Navigator,
    private val fragmentManager: FragmentManager
) : BackButtonController {

    override fun onBackPressed() {
        val topFragment = fragmentManager.fragments.lastOrNull()
        val consumed = (topFragment as? BackButtonHandler)?.onBackPressed()
        when (consumed) {
            null -> Timber.i("Top fragment could not handle back press")
            true -> Timber.i("Top fragment handled back press")
            false -> Timber.i("Top fragment did not handle back press")
        }
        if (consumed != true) navigator.closeScreen()
    }
}

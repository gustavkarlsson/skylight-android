package se.gustavkarlsson.skylight.android.lib.navigationsetup

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import se.gustavkarlsson.skylight.android.lib.navigation.Backstack
import se.gustavkarlsson.skylight.android.lib.navigation.BackstackListener
import se.gustavkarlsson.skylight.android.lib.navigation.NavigationOverride
import se.gustavkarlsson.skylight.android.lib.navigation.Navigator

internal object ViewModelNavigationInstaller : NavigationInstaller {
    override fun install(
        activity: AppCompatActivity,
        initialBackstack: Backstack,
        navigationOverrides: Iterable<NavigationOverride>,
        backstackListeners: List<BackstackListener>,
    ): Pair<Navigator, BackButtonController> {
        val viewModelFactory = ViewModelNavigator.Factory(
            activity,
            initialBackstack,
            navigationOverrides,
            backstackListeners,
        )
        val navigator = ViewModelProvider(activity, viewModelFactory).get(ViewModelNavigator::class.java)
        val backButtonController = NavigatorBackButtonController(navigator, activity)
        return navigator to backButtonController
    }
}

package se.gustavkarlsson.skylight.android.lib.navigationsetup

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import kotlinx.coroutines.ExperimentalCoroutinesApi
import se.gustavkarlsson.skylight.android.lib.navigation.Backstack
import se.gustavkarlsson.skylight.android.lib.navigation.NavigationOverride
import se.gustavkarlsson.skylight.android.lib.navigation.Navigator

internal object ViewModelNavigationInstaller : NavigationInstaller {
    @ExperimentalCoroutinesApi
    override fun install(
        activity: AppCompatActivity,
        initialBackstack: Backstack,
        navigationOverrides: Iterable<NavigationOverride>,
    ): Navigator {
        val viewModelFactory = ViewModelNavigator.Factory(activity, initialBackstack, navigationOverrides)
        return ViewModelProvider(activity, viewModelFactory).get<ViewModelNavigator>()
    }
}

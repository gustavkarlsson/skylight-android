package se.gustavkarlsson.skylight.android.lib.navigationsetup

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import se.gustavkarlsson.skylight.android.lib.navigation.Backstack
import se.gustavkarlsson.skylight.android.lib.navigation.NavigationOverride

internal object ViewModelNavigationInstaller : NavigationInstaller {
    override fun install(
        activity: AppCompatActivity,
        initialBackstack: Backstack,
        navigationOverrides: Iterable<NavigationOverride>,
    ): MasterNavigator {
        val viewModelFactory = ViewModelNavigator.Factory(activity, initialBackstack, navigationOverrides)
        return ViewModelProvider(activity, viewModelFactory).get<ViewModelNavigator>()
    }
}

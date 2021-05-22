package se.gustavkarlsson.skylight.android.lib.navigationsetup

import androidx.appcompat.app.AppCompatActivity
import se.gustavkarlsson.skylight.android.lib.navigation.Backstack
import se.gustavkarlsson.skylight.android.lib.navigation.NavigationOverride

interface NavigationInstaller {
    fun install(
        activity: AppCompatActivity,
        initialBackstack: Backstack,
        navigationOverrides: Iterable<NavigationOverride> = emptyList(),
    ): MasterNavigator
}

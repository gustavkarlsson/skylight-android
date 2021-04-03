package se.gustavkarlsson.skylight.android.lib.navigationsetup

import androidx.appcompat.app.AppCompatActivity
import se.gustavkarlsson.skylight.android.lib.navigation.Backstack
import se.gustavkarlsson.skylight.android.lib.navigation.NavigationOverride
import se.gustavkarlsson.skylight.android.lib.navigation.Navigator

interface NavigationInstaller {
    fun install(
        activity: AppCompatActivity,
        initialBackstack: Backstack,
        navigationOverrides: Iterable<NavigationOverride> = emptyList(),
    ): Navigator
}

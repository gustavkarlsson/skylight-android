package se.gustavkarlsson.skylight.android.lib.navigationsetup

import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import se.gustavkarlsson.skylight.android.lib.navigation.Backstack
import se.gustavkarlsson.skylight.android.lib.navigation.BackstackListener
import se.gustavkarlsson.skylight.android.lib.navigation.NavigationOverride
import se.gustavkarlsson.skylight.android.lib.navigation.Navigator

interface NavigationInstaller {
    fun install(
        activity: AppCompatActivity,
        @IdRes containerId: Int,
        initialBackstack: Backstack,
        navigationOverrides: Iterable<NavigationOverride> = emptyList(),
        backstackListeners: List<BackstackListener> = emptyList(),
        animationConfig: AnimationConfig = AnimationConfig()
    ): Pair<Navigator, BackButtonController>
}

package se.gustavkarlsson.skylight.android.lib.navigationsetup

import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import se.gustavkarlsson.skylight.android.lib.navigation.newer.Backstack
import se.gustavkarlsson.skylight.android.lib.navigation.newer.BackstackListener
import se.gustavkarlsson.skylight.android.lib.navigation.newer.NavigationOverride
import se.gustavkarlsson.skylight.android.lib.navigation.newer.Navigator

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

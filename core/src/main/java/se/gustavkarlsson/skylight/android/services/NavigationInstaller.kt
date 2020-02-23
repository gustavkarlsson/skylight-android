package se.gustavkarlsson.skylight.android.services

import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import se.gustavkarlsson.skylight.android.entities.AnimationConfig
import se.gustavkarlsson.skylight.android.navigation.Backstack
import se.gustavkarlsson.skylight.android.navigation.BackstackListener
import se.gustavkarlsson.skylight.android.navigation.NavigationOverride
import se.gustavkarlsson.skylight.android.navigation.Navigator

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

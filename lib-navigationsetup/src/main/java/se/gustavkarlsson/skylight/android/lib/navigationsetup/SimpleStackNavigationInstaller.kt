package se.gustavkarlsson.skylight.android.lib.navigationsetup

import android.app.Activity
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import se.gustavkarlsson.skylight.android.entities.AnimationConfig
import se.gustavkarlsson.skylight.android.navigation.Backstack
import se.gustavkarlsson.skylight.android.navigation.BackstackListener
import se.gustavkarlsson.skylight.android.navigation.NavigationOverride
import se.gustavkarlsson.skylight.android.navigation.Navigator
import se.gustavkarlsson.skylight.android.services.BackButtonController
import com.zhuinden.simplestack.Backstack as SSBackstack
import com.zhuinden.simplestack.navigator.Navigator as SSNavigator

internal class SimpleStackNavigationInstaller(
    private val directionsCalculator: DirectionsCalculator
) : NavigationInstaller {
    override fun install(
        activity: AppCompatActivity,
        @IdRes containerId: Int,
        initialBackstack: Backstack,
        navigationOverrides: Iterable<NavigationOverride>,
        backstackListeners: List<BackstackListener>,
        animationConfig: AnimationConfig
    ): Pair<Navigator, BackButtonController> {
        val fragmentManager = activity.supportFragmentManager
        val navigationOverride =
            AggregatingNavigationOverride(
                navigationOverrides
            )
        val stateChanger =
            FragmentStateChanger(
                fragmentManager,
                containerId,
                animationConfig,
                backstackListeners
            )
        val backstack = activity.installBackstack(
            containerId,
            initialBackstack,
            navigationOverride,
            stateChanger
        )
        val navigator =
            SimpleStackNavigator(
                backstack,
                navigationOverride,
                directionsCalculator,
                activity::finish
            )
        val backButtonController =
            NavigatorBackButtonController(
                navigator,
                fragmentManager
            )
        return navigator to backButtonController
    }
}

private fun Activity.installBackstack(
    containerId: Int,
    initialBackstack: Backstack,
    navigationOverride: NavigationOverride,
    stateChanger: FragmentStateChanger
): SSBackstack {
    val container = findViewById<ViewGroup>(containerId)
    val finalBackstack =
        navigationOverride.override(emptyList(), initialBackstack) ?: initialBackstack
    return SSNavigator
        .configure()
        .setStateChanger(stateChanger)
        .install(this, container, finalBackstack)
}

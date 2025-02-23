package se.gustavkarlsson.skylight.android.lib.navigation

import arrow.core.NonEmptyList
import arrow.core.getOrElse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import me.tatarka.inject.annotations.Inject
import se.gustavkarlsson.skylight.android.core.logging.logError
import se.gustavkarlsson.skylight.android.core.logging.logInfo
import se.gustavkarlsson.skylight.android.core.utils.nonEmpty

@Inject
@NavigationScope // TODO Scope to Activity instead using a ViewModel?
internal class DefaultNavigator(
    private val defaultScreen: Screen,
    private val overrides: Set<NavigationOverride>,
) : Navigator {

    private val mutableBackstackChanges = let {
        val targetBackstack = Backstack(defaultScreen)
        val newBackstack = overrideBackstack(targetBackstack, targetBackstack)
        logInfo { "Setting backstack to: $newBackstack" }
        MutableStateFlow(BackstackChange(newBackstack, newBackstack))
    }

    override val backstackChanges: StateFlow<BackstackChange> = mutableBackstackChanges

    override fun setBackstack(newBackstack: Backstack) = changeBackstack {
        logInfo { "Setting backstack to $newBackstack" }
        newBackstack.screens
    }

    override fun goTo(screen: Screen) = changeBackstack { screens ->
        logInfo { "Going to $screen" }
        screens + screen
    }

    override fun closeScreenAndGoTo(screen: Screen) = changeBackstack { screens ->
        logInfo { "Closing screen and going to $screen" }
        val remaining = screens.dropLast(1)
        remaining + screen
    }

    override fun closeScopeAndGoTo(scope: String, screen: Screen) = changeBackstack { screens ->
        logInfo { "Closing scope '$scope' and going to $screen" }
        val remaining = screens.takeWhile { it.scopeStart != scope }
        remaining + screen
    }

    override fun closeScreen() = changeBackstack { screens ->
        logInfo { "Closing screen" }
        screens.dropLast(1)
    }

    override fun closeScope(scope: String) = changeBackstack { screens ->
        logInfo { "Closing scope '$scope'" }
        screens.takeWhile { it.scopeStart != scope }
    }

    // TODO Atomic update
    private fun changeBackstack(change: (screens: NonEmptyList<Screen>) -> List<Screen>) {
        val oldBackstack = backstackChanges.value.new
        val changedScreens = change(oldBackstack.screens)
        val targetScreens = changedScreens.nonEmpty().getOrElse {
            logError { "Attempt to clear entire backstack detected. Aborting navigation" }
            return
        }
        val targetBackstack = Backstack(targetScreens)
        val newBackstack = overrideBackstack(oldBackstack, targetBackstack)
        logInfo { "Setting backstack to: $newBackstack" }
        mutableBackstackChanges.value = BackstackChange(oldBackstack, newBackstack)
    }

    private fun overrideBackstack(oldBackstack: Backstack, newBackstack: Backstack): Backstack {
        val overridden = overrides.asSequence()
            .sortedByDescending { it.priority }
            .mapNotNull { it.override(oldBackstack, newBackstack) }
            .firstOrNull()
        if (overridden != null) {
            logInfo { "Overrode $oldBackstack with $overridden instead of $newBackstack" }
        }
        return overridden ?: newBackstack
    }
}

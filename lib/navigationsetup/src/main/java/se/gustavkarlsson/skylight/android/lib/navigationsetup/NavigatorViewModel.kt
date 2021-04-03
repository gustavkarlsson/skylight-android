package se.gustavkarlsson.skylight.android.lib.navigationsetup

import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import se.gustavkarlsson.skylight.android.core.logging.logInfo
import se.gustavkarlsson.skylight.android.lib.navigation.Backstack
import se.gustavkarlsson.skylight.android.lib.navigation.BackstackListener
import se.gustavkarlsson.skylight.android.lib.navigation.NavigationOverride
import se.gustavkarlsson.skylight.android.lib.navigation.Navigator
import se.gustavkarlsson.skylight.android.lib.navigation.Screen

private const val STATE_BACKSTACK_KEY = "backstack"

internal class NavigatorViewModel private constructor(
    private val state: SavedStateHandle,
    initialBackstack: Backstack,
    private val overrides: Iterable<NavigationOverride>,
    private val backstackListeners: List<BackstackListener>,
) : ViewModel(), Navigator {

    override val backstack: LiveData<Backstack>

    init {
        val actualInitialBackstack = overrideBackstack(emptyList(), initialBackstack)
        updateBackstackListeners(emptyList(), actualInitialBackstack)
        backstack = state.getLiveData(STATE_BACKSTACK_KEY, actualInitialBackstack)
    }

    override fun setBackstack(backstack: Backstack) = changeBackstack {
        logInfo { "Setting backstack to $backstack" }
        backstack
    }

    override fun goTo(screen: Screen) = changeBackstack { stack ->
        logInfo { "Going to $screen" }
        stack + screen
    }

    override fun closeScreenAndGoTo(screen: Screen) = changeBackstack { stack ->
        logInfo { "Closing screen and going to $screen" }
        val remaining = stack.dropLast(1)
        remaining + screen
    }

    override fun closeScopeAndGoTo(scope: String, screen: Screen) = changeBackstack { stack ->
        logInfo { "Closing scope '$scope' and going to $screen" }
        val remaining = stack.takeWhile { it.scopeStart != scope }
        remaining + screen
    }

    override fun closeScreen() = changeBackstack { stack ->
        logInfo { "Closing screen" }
        stack.dropLast(1)
    }

    override fun closeScope(scope: String) = changeBackstack { stack ->
        logInfo { "Closing scope '$scope'" }
        stack.takeWhile { it.scopeStart != scope }
    }

    private fun changeBackstack(change: (stack: Backstack) -> Backstack) {
        val oldBackstack = backstack.value ?: emptyList()
        val targetBackstack = change(oldBackstack)
        val newBackstack = overrideBackstack(oldBackstack, targetBackstack)
        logInfo { "Backstack is now: $newBackstack" }
        updateBackstackListeners(oldBackstack, newBackstack)
        state.set(STATE_BACKSTACK_KEY, newBackstack)
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

    private fun updateBackstackListeners(oldBackstack: Backstack, newBackstack: Backstack) {
        for (listener in backstackListeners) {
            listener.onBackstackChanged(oldBackstack, newBackstack)
        }
    }

    class Factory(
        owner: SavedStateRegistryOwner,
        private val initialBackstack: Backstack,
        private val navigationOverrides: Iterable<NavigationOverride>,
        private val backstackListeners: List<BackstackListener>,
    ) : AbstractSavedStateViewModelFactory(owner, null) {
        override fun <T : ViewModel> create(key: String, modelClass: Class<T>, handle: SavedStateHandle): T {
            @Suppress("UNCHECKED_CAST")
            return NavigatorViewModel(handle, initialBackstack, navigationOverrides, backstackListeners) as T
        }
    }
}

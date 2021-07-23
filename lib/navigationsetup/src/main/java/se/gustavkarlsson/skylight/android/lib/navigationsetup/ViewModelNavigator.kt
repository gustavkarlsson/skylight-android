package se.gustavkarlsson.skylight.android.lib.navigationsetup

import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import androidx.savedstate.SavedStateRegistryOwner
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.flow.stateIn
import se.gustavkarlsson.skylight.android.core.logging.logInfo
import se.gustavkarlsson.skylight.android.lib.navigation.BackPress
import se.gustavkarlsson.skylight.android.lib.navigation.Backstack
import se.gustavkarlsson.skylight.android.lib.navigation.BackstackChange
import se.gustavkarlsson.skylight.android.lib.navigation.NavigationOverride
import se.gustavkarlsson.skylight.android.lib.navigation.Screen

private const val STATE_BACKSTACK_KEY = "backstack"

@OptIn(ExperimentalCoroutinesApi::class)
internal class ViewModelNavigator private constructor(
    private val state: SavedStateHandle,
    initialBackstack: Backstack,
    private val overrides: Iterable<NavigationOverride>,
) : ViewModel(), MasterNavigator {

    override val backstackChanges: StateFlow<BackstackChange>

    init {
        val actualInitialBackstack = overrideBackstack(emptyList(), initialBackstack)
        val initialChange = BackstackChange(emptyList(), actualInitialBackstack)
        backstackChanges = state.getLiveData(STATE_BACKSTACK_KEY, actualInitialBackstack)
            .asFlow()
            .scan(initialChange) { previous, current ->
                BackstackChange(previous.new, current)
            }
            .filter { it.old != it.new }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), initialValue = initialChange)
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

    override fun onBackPress() {
        val topScreen = backstackChanges.value.new.lastOrNull()
        val result = topScreen?.onBackPress()
        when (result) {
            null -> logInfo { "No top screen to handle back press" }
            BackPress.HANDLED -> logInfo { "Top screen handled back press" }
            BackPress.NOT_HANDLED -> logInfo { "Top screen did not handle back press" }
        }
        if (result != BackPress.HANDLED) closeScreen()
    }

    private fun changeBackstack(change: (stack: Backstack) -> Backstack) {
        val oldBackstack = backstackChanges.value.new
        val targetBackstack = change(oldBackstack)
        val newBackstack = overrideBackstack(oldBackstack, targetBackstack)
        logInfo { "Backstack is now: $newBackstack" }
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

    class Factory(
        owner: SavedStateRegistryOwner,
        private val initialBackstack: Backstack,
        private val navigationOverrides: Iterable<NavigationOverride>,
    ) : AbstractSavedStateViewModelFactory(owner, null) {
        override fun <T : ViewModel> create(key: String, modelClass: Class<T>, handle: SavedStateHandle): T {
            @Suppress("UNCHECKED_CAST")
            return ViewModelNavigator(handle, initialBackstack, navigationOverrides) as T
        }
    }
}

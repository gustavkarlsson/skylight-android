package se.gustavkarlsson.skylight.android.lib.navigation

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import se.gustavkarlsson.skylight.android.core.logging.logInfo

@OptIn(ExperimentalCoroutinesApi::class)
internal class DefaultNavigator(
    private val overrides: Iterable<NavigationOverride>,
) : Navigator, BackPressHandler {

    private val mutableBackstackChanges = MutableStateFlow(BackstackChange(emptyList(), emptyList()))

    override val backstackChanges = mutableBackstackChanges

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
        val result = topScreen?.onBackPress()
        when (result) {
            null -> logInfo { "No top screen to handle back press" }
            BackPress.HANDLED -> logInfo { "Top screen handled back press" }
            BackPress.NOT_HANDLED -> logInfo { "Top screen did not handle back press" }
        }
        if (result != BackPress.HANDLED) closeScreen()
    }

    // TODO Atomic update
    private fun changeBackstack(change: (stack: Backstack) -> Backstack) {
        val oldBackstack = backstackChanges.value.new
        val targetBackstack = change(oldBackstack)
        val newBackstack = overrideBackstack(oldBackstack, targetBackstack)
        logInfo { "Backstack is now: $newBackstack" }
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

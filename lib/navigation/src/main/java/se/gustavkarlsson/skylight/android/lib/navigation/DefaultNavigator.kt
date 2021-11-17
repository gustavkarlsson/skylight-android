package se.gustavkarlsson.skylight.android.lib.navigation

import arrow.core.NonEmptyList
import arrow.core.getOrElse
import arrow.core.nonEmptyListOf
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import se.gustavkarlsson.skylight.android.core.logging.logInfo
import se.gustavkarlsson.skylight.android.core.utils.nonEmpty

internal class DefaultNavigator(
    private val defaultScreen: Screen,
    private val overrides: Iterable<NavigationOverride>,
) : Navigator, BackPressHandler {

    private val mutableBackstackChanges = let {
        val targetBackstack = Backstack(defaultScreen)
        val newBackstack = overrideBackstack(targetBackstack, targetBackstack)
        logInfo { "Setting initial backstack to: $newBackstack" }
        MutableStateFlow(BackstackChange(newBackstack, newBackstack))
    }

    override val backstackChanges = mutableBackstackChanges

    private val mutableLeaveFlow = MutableSharedFlow<Unit>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_LATEST,
    )

    override val leave = mutableLeaveFlow

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

    override fun onBackPress() {
        when (currentScreens.head.onBackPress()) {
            BackPress.HANDLED -> {
                logInfo { "Top screen handled back press" }
            }
            BackPress.NOT_HANDLED -> {
                logInfo { "Top screen did not handle back press" }
                closeScreen()
            }
        }
    }

    // TODO Atomic update
    private fun changeBackstack(change: (screens: NonEmptyList<Screen>) -> List<Screen>) {
        val oldBackstack = backstackChanges.value.new
        val changedScreens = change(oldBackstack.screens)
        val targetScreens = changedScreens.nonEmpty().getOrElse {
            logInfo { "Nothing more on backstack, resetting to $defaultScreen and leaving" }
            mutableLeaveFlow.tryEmit(Unit)
            nonEmptyListOf(defaultScreen)
        }
        val targetBackstack = Backstack(targetScreens)
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

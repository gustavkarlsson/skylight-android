package se.gustavkarlsson.skylight.android.feature.intro

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.runBlocking
import se.gustavkarlsson.skylight.android.core.Io
import se.gustavkarlsson.skylight.android.lib.navigation.Backstack
import se.gustavkarlsson.skylight.android.lib.navigation.NavigationOverride
import se.gustavkarlsson.skylight.android.lib.navigation.Screen
import se.gustavkarlsson.skylight.android.lib.runversion.RunVersionManager
import javax.inject.Inject

internal class IntroNavigationOverride @Inject constructor(
    @Io private val dispatcher: CoroutineDispatcher,
    private val runVersionManager: RunVersionManager,
) : NavigationOverride {
    override val priority = 10

    override fun override(oldBackstack: Backstack, targetBackstack: Backstack): Backstack? =
        runBlocking(dispatcher) { // TODO Do we have to run blocking?
            when {
                targetBackstack.screens.none { it.type == Screen.Type.Intro } &&
                    runVersionManager.isFirstRun -> Backstack(IntroScreen(targetBackstack))
                else -> null
            }
        }
}

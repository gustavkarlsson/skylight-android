package se.gustavkarlsson.skylight.android.feature.intro

import kotlinx.coroutines.runBlocking
import se.gustavkarlsson.skylight.android.lib.navigation.Backstack
import se.gustavkarlsson.skylight.android.lib.navigation.NavigationOverride
import se.gustavkarlsson.skylight.android.lib.navigation.Screen
import se.gustavkarlsson.skylight.android.lib.runversion.RunVersionManager
import javax.inject.Inject

internal class IntroNavigationOverride @Inject constructor(
    private val runVersionManager: RunVersionManager,
) : NavigationOverride {
    override val priority = 10

    override fun override(oldBackstack: Backstack, targetBackstack: Backstack): Backstack? {
        val introScreenOnStack = targetBackstack.screens.any { it.type == Screen.Type.Intro }
        return when {
            !introScreenOnStack && isFirstRun() -> Backstack(IntroScreen(targetBackstack))
            else -> null
        }
    }

    private fun isFirstRun() = runBlocking { runVersionManager.isFirstRun() }
}

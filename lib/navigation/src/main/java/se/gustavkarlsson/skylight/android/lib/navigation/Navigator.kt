package se.gustavkarlsson.skylight.android.lib.navigation

import kotlinx.coroutines.flow.StateFlow

interface Navigator {
    val backstack: StateFlow<Backstack>
    fun setBackstack(backstack: Backstack)
    fun goTo(screen: Screen)
    fun closeScreenAndGoTo(screen: Screen)
    fun closeScopeAndGoTo(scope: String, screen: Screen)
    fun closeScreen()
    fun closeScope(scope: String)
}

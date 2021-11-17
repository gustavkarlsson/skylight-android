package se.gustavkarlsson.skylight.android.lib.navigation

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface Navigator {
    val backstackChanges: StateFlow<BackstackChange>
    val leave: Flow<Unit>
    fun setBackstack(newBackstack: Backstack)
    fun goTo(screen: Screen)
    fun closeScreenAndGoTo(screen: Screen)
    fun closeScopeAndGoTo(scope: String, screen: Screen)
    fun closeScreen()
    fun closeScope(scope: String)
}

val Navigator.currentBackstack: Backstack get() = backstackChanges.value.new

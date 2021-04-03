package se.gustavkarlsson.skylight.android.lib.navigation

import androidx.lifecycle.LiveData

interface Navigator {
    val backstack: LiveData<Backstack> // TODO StateFlow instead?
    fun setBackstack(backstack: Backstack)
    fun goTo(screen: Screen)
    fun closeScreenAndGoTo(screen: Screen)
    fun closeScopeAndGoTo(scope: String, screen: Screen)
    fun closeScreen()
    fun closeScope(scope: String)
}

package se.gustavkarlsson.skylight.android.lib.navigation.newer

interface Navigator {
    val backstack: Backstack
    fun setBackstack(backstack: Backstack)
    fun goTo(screen: Screen)
    fun closeScreenAndGoTo(screen: Screen)
    fun closeScopeAndGoTo(scope: String, screen: Screen)
    fun closeScreen()
    fun closeScope(scope: String)
}

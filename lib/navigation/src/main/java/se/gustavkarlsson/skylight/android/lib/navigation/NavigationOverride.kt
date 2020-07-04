package se.gustavkarlsson.skylight.android.lib.navigation

interface NavigationOverride {
    val priority: Int
    fun override(oldBackstack: Backstack, targetBackstack: Backstack): Backstack?
}

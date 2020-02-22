package se.gustavkarlsson.skylight.android.lib.navigation

interface NavigationOverride {
    val priority: Int
    fun override(backstack: Backstack): Backstack?
}

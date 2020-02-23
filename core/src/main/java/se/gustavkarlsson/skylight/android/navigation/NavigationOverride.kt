package se.gustavkarlsson.skylight.android.navigation

interface NavigationOverride {
    val priority: Int
    fun override(backstack: Backstack): Backstack?
}

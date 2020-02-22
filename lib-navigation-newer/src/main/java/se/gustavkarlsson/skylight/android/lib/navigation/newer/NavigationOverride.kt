package se.gustavkarlsson.skylight.android.lib.navigation.newer

interface NavigationOverride {
    val priority: Int
    fun override(backstack: Backstack): Backstack?
}

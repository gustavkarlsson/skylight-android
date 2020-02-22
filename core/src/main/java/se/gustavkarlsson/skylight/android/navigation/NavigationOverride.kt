package se.gustavkarlsson.skylight.android.navigation

import se.gustavkarlsson.skylight.android.navigation.Backstack

interface NavigationOverride {
    val priority: Int
    fun override(backstack: Backstack): Backstack?
}

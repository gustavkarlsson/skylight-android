package se.gustavkarlsson.skylight.android.navigation

import se.gustavkarlsson.skylight.android.navigation.Backstack
import se.gustavkarlsson.skylight.android.navigation.Screen

interface Screens {
    val main: Screen
    fun addPlace(target: Backstack? = null): Screen
    val settings: Screen
    val about: Screen
}

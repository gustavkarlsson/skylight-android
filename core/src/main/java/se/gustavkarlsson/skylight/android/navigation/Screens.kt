package se.gustavkarlsson.skylight.android.navigation

interface Screens {
    val main: Screen
    fun addPlace(target: Backstack? = null): Screen
    val settings: Screen
    val about: Screen
}

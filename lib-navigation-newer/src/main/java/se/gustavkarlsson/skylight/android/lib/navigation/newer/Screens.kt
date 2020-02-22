package se.gustavkarlsson.skylight.android.lib.navigation.newer

interface Screens {
    val main: Screen
    fun addPlace(target: Backstack? = null): Screen
    val settings: Screen
    val about: Screen
}

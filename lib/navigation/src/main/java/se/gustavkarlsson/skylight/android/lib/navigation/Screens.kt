package se.gustavkarlsson.skylight.android.lib.navigation

interface Screens {
    val main: Screen
    fun addPlace(target: Backstack? = null): Screen
    val settings: Screen
    val about: Screen
    val privacyPolicy: Screen
}

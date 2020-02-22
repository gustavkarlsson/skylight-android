package se.gustavkarlsson.skylight.android.lib.navigation.newer

interface Screens {
    val main: Screen
    val account: Screen
    val stateshare: Screen
    fun scope(number: Int): Screen
}

package se.gustavkarlsson.skylight.android.lib.navigation

interface BackButtonHandler {
    fun onBackPressed(): BackPress
}

enum class BackPress {
    HANDLED, NOT_HANDLED
}

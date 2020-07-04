package se.gustavkarlsson.skylight.android.lib.navigationsetup

import se.gustavkarlsson.skylight.android.lib.navigation.Backstack

internal interface DirectionsCalculator {
    fun getDirection(oldBackstack: Backstack, newBackstack: Backstack): Int
}

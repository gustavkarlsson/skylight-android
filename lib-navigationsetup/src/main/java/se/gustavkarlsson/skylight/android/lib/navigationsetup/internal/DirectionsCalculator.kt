package se.gustavkarlsson.skylight.android.lib.navigationsetup.internal

import se.gustavkarlsson.skylight.android.navigation.Backstack

internal interface DirectionsCalculator {
    fun getDirection(oldBackstack: Backstack, newBackstack: Backstack): Int
}

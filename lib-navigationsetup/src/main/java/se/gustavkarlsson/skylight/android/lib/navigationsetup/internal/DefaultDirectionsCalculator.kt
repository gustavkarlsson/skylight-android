package se.gustavkarlsson.skylight.android.lib.navigationsetup.internal

import se.gustavkarlsson.skylight.android.navigation.Backstack
import se.gustavkarlsson.skylight.android.navigation.Screen
import com.zhuinden.simplestack.StateChange

internal object DefaultDirectionsCalculator : DirectionsCalculator {

    override fun getDirection(oldBackstack: Backstack, newBackstack: Backstack) =
        when {
            oldBackstack.isEmpty() -> StateChange.REPLACE
            oldBackstack.containsTopOf(newBackstack) -> StateChange.BACKWARD
            newBackstack.containsTopOf(oldBackstack) -> StateChange.FORWARD
            else -> StateChange.REPLACE
        }
}

private fun Backstack.containsTopOf(other: Backstack): Boolean {
    val tags = this.map(Screen::tag)
    val otherTop = other.lastOrNull()?.tag ?: return false
    return otherTop in tags
}

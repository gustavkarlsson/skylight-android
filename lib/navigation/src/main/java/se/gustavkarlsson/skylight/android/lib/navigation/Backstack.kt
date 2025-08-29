package se.gustavkarlsson.skylight.android.lib.navigation

import android.os.Parcelable
import arrow.core.NonEmptyList
import arrow.core.nonEmptyListOf
import kotlinx.parcelize.Parcelize
import se.gustavkarlsson.skylight.android.core.utils.nonEmptyUnsafe

@Parcelize
class Backstack private constructor(private val _screens: List<Screen>) : Parcelable {
    val screens: NonEmptyList<Screen> get() = _screens.nonEmptyUnsafe()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Backstack

        return _screens == other._screens
    }

    override fun hashCode(): Int {
        return _screens.hashCode()
    }

    override fun toString(): String {
        return "Backstack(screens=$screens)"
    }

    companion object {
        fun ofScreens(screens: NonEmptyList<Screen>) = Backstack(screens.toList())
        fun ofScreen(screen: Screen) = Backstack(nonEmptyListOf(screen))
    }
}

fun Backstack.update(block: (NonEmptyList<Screen>) -> NonEmptyList<Screen>): Backstack {
    val newScreens = block(screens)
    return Backstack.ofScreens(newScreens)
}

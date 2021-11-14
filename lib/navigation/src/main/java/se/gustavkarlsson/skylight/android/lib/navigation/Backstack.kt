package se.gustavkarlsson.skylight.android.lib.navigation

import android.os.Parcelable
import arrow.core.NonEmptyList
import arrow.core.nonEmptyListOf
import kotlinx.parcelize.Parcelize
import se.gustavkarlsson.skylight.android.core.utils.nonEmptyUnsafe

@Parcelize
class Backstack private constructor(private val _screens: List<Screen>) : Parcelable {
    constructor(screens: NonEmptyList<Screen>) : this(screens.toList())
    constructor(screen: Screen) : this(nonEmptyListOf(screen))

    val screens: NonEmptyList<Screen> get() = _screens.nonEmptyUnsafe()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Backstack

        if (_screens != other._screens) return false

        return true
    }

    override fun hashCode(): Int {
        return _screens.hashCode()
    }

    override fun toString(): String {
        return "Backstack(screens=$screens)"
    }
}

fun Backstack.update(block: (NonEmptyList<Screen>) -> NonEmptyList<Screen>): Backstack {
    val newScreens = block(screens)
    return Backstack(newScreens)
}

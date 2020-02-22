package se.gustavkarlsson.skylight.android.navigation

import se.gustavkarlsson.skylight.android.navigation.Backstack

interface BackstackListener {
    fun onBackstackChanged(old: Backstack, new: Backstack)
}

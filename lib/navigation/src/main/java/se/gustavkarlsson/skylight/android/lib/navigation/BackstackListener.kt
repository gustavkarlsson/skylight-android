package se.gustavkarlsson.skylight.android.lib.navigation

interface BackstackListener {
    fun onBackstackChanged(old: Backstack, new: Backstack)
}

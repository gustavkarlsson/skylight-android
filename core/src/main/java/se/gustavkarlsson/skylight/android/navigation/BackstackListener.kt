package se.gustavkarlsson.skylight.android.navigation

interface BackstackListener {
    fun onBackstackChanged(old: Backstack, new: Backstack)
}

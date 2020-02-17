package se.gustavkarlsson.skylight.android.lib.navigation

import androidx.fragment.app.Fragment

interface FragmentFactory {
    fun createFragment(name: String): Fragment?
}

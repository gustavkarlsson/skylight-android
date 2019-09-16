package se.gustavkarlsson.skylight.android.lib.navigation

import androidx.fragment.app.Fragment

interface FragmentFactoryDirectory {
	fun register(fragmentFactory: FragmentFactory)
	fun getFragment(navItem: NavItem): Fragment?
}

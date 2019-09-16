package se.gustavkarlsson.skylight.android.lib.navigation

import androidx.fragment.app.Fragment

internal class FragmentFactoryDirectoryImpl : FragmentFactoryDirectory {
	private val factories = mutableListOf<FragmentFactory>()

	override fun register(fragmentFactory: FragmentFactory) {
		factories += fragmentFactory
	}

	override fun getFragment(navItem: NavItem): Fragment? =
		factories.asSequence().map { it.createFragment(navItem) }.firstOrNull()
}

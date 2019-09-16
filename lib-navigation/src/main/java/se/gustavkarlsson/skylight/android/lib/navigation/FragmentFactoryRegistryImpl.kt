package se.gustavkarlsson.skylight.android.lib.navigation

import androidx.fragment.app.Fragment

internal class FragmentFactoryRegistryImpl : FragmentFactory, FragmentFactoryRegistry {
	private val factories = mutableListOf<FragmentFactory>()

	override fun register(fragmentFactory: FragmentFactory) {
		factories += fragmentFactory
	}

	override fun createFragment(navItem: NavItem): Fragment? =
		factories
			.asSequence()
			.mapNotNull { it.createFragment(navItem) }
			.firstOrNull()
}

package se.gustavkarlsson.skylight.android.lib.navigation

internal class NavItemOverrideRegistryImpl : NavItemOverrideRegistry, NavItemOverride {
    private val overrides = mutableListOf<NavItemOverride>()

    override fun register(navItemOverride: NavItemOverride) {
        overrides += navItemOverride
    }

    override fun override(item: NavItem): NavItem? =
        overrides
            .sortedByDescending { it.priority }
            .asSequence()
            .mapNotNull { it.override(item) }
            .firstOrNull()
}

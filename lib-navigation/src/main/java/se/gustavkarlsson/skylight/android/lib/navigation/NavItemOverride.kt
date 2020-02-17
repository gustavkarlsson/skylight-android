package se.gustavkarlsson.skylight.android.lib.navigation

interface NavItemOverride {
    val priority: Int get() = Int.MIN_VALUE
    fun override(item: NavItem): NavItem?
}

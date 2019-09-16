package se.gustavkarlsson.skylight.android.lib.navigation

interface NavItemOverride {
	val priority: Int get() = 0
	fun override(item: NavItem): NavItem?
}

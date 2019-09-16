package se.gustavkarlsson.skylight.android.lib.navigation

interface Navigator {
	fun push(item: NavItem)
	fun replace(item: NavItem)
	fun replaceScope(item: NavItem)
	fun pop()
	fun popScope()
}

package se.gustavkarlsson.skylight.android.lib.navigation

import android.os.Bundle

interface Navigator {
	val backStackSize: Int
	fun push(item: NavItem)
	fun replace(item: NavItem)
	fun replaceScope(item: NavItem)
	fun pop(arguments: Bundle? = null)
	fun popScope(arguments: Bundle? = null)
	fun onBackPressed()
}

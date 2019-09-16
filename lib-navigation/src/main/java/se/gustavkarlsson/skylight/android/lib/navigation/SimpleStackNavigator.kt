package se.gustavkarlsson.skylight.android.lib.navigation

import android.app.Activity
import com.zhuinden.simplestack.Backstack
import com.zhuinden.simplestack.StateChange

internal class SimpleStackNavigator(
	private val activity: Activity,
	private val backstack: Backstack,
	private val navItemOverride: NavItemOverride
) : Navigator {

	override fun push(item: NavItem) = changeHistory {
		this + item
	}

	override fun replace(item: NavItem) = changeHistory {
		dropLast(1) + item
	}

	override fun replaceScope(item: NavItem) = changeHistory {
		val topScope = lastOrNull()?.scope ?: ""
		dropLastWhile { it.scope == topScope } + item
	}

	override fun pop() = changeHistory {
		dropLast(1)
	}

	override fun popScope() = changeHistory {
		val topScope = lastOrNull()?.scope ?: ""
		dropLastWhile { it.scope == topScope }
	}

	private fun changeHistory(change: List<NavItem>.() -> List<NavItem>) {
		val oldHistory = backstack.getHistory<NavItem>().toList()
		val newHistory = oldHistory.change().override()
		val direction = when {
			newHistory.size > oldHistory.size -> StateChange.FORWARD
			newHistory.size < oldHistory.size -> StateChange.BACKWARD
			else -> StateChange.REPLACE
		}
		if (newHistory.isEmpty()) activity.finish()
		else backstack.setHistory(newHistory, direction)
	}

	private fun List<NavItem>.override(): List<NavItem> {
		val top = lastOrNull() ?: return this
		val overridden = navItemOverride.override(top) ?: return this
		return dropLast(1) + overridden
	}
}

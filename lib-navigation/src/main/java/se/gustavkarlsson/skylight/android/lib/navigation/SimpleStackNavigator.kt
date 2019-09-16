package se.gustavkarlsson.skylight.android.lib.navigation

import com.zhuinden.simplestack.Backstack
import com.zhuinden.simplestack.StateChange

internal class SimpleStackNavigator(private val backstack: Backstack) : Navigator {

	private val history get() = backstack.getHistory<NavItem>().toList()

	override fun push(item: NavItem) =
		backstack.setHistory(history + item, StateChange.FORWARD)

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
		val newHistory = oldHistory.change()
		val direction = when {
			newHistory.size > oldHistory.size -> StateChange.FORWARD
			newHistory.size < oldHistory.size -> StateChange.BACKWARD
			else -> StateChange.REPLACE
		}
		backstack.setHistory(newHistory, direction)
	}
}

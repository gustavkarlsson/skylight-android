package se.gustavkarlsson.skylight.android.lib.navigation

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.FragmentManager
import com.zhuinden.simplestack.Backstack
import com.zhuinden.simplestack.StateChange

internal class SimpleStackNavigator(
	private val activity: Activity,
	private val fragmentManager: FragmentManager,
	private val backstack: Backstack,
	private val navItemOverride: NavItemOverride
) : Navigator {
	override val backStackSize get() = backstack.getHistory<Any?>().size

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

	override fun pop(arguments: Bundle?) = changeHistory {
		val popped = dropLast(1)
		popped.also { tryMergeTopArguments(arguments) }
	}

	override fun popScope(arguments: Bundle?) = changeHistory {
		val topScope = lastOrNull()?.scope ?: ""
		val popped = dropLastWhile { it.scope == topScope }
		popped.also { tryMergeTopArguments(arguments) }
	}

	private fun List<NavItem>.tryMergeTopArguments(arguments: Bundle?) {
		if (isNotEmpty() && arguments != null)
			last().arguments.putAll(arguments)
	}

	private fun changeHistory(change: List<NavItem>.() -> List<NavItem>) {
		val oldHistory = backstack.getHistory<NavItem>().dropWhile { it == NavItem.EMPTY }
		val newHistory = oldHistory.change().override()
		val direction = when {
			oldHistory.isEmpty() -> StateChange.REPLACE
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

	override fun onBackPressed() {
		val topFragment = fragmentManager.fragments.lastOrNull()
		if ((topFragment as? BackButtonHandler)?.onBackPressed() == true) return
		pop()
	}
}

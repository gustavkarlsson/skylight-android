package se.gustavkarlsson.skylight.android.lib.navigation

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.zhuinden.simplestack.StateChange
import com.zhuinden.simplestack.StateChanger

// Stolen from https://github.com/Zhuinden/simple-stack/blob/6f72e80/simple-stack-example-basic-kotlin-fragment/src/main/java/com/zhuinden/simplestackexamplekotlinfragment/core/navigation/FragmentStateChanger.kt
internal class FragmentStateChanger(
	private val fragmentManager: FragmentManager,
	private val fragmentFactory: FragmentFactory,
	private val containerId: Int
) : StateChanger {
	override fun handleStateChange(
		stateChange: StateChange,
		completionCallback: StateChanger.Callback
	) {
		if (stateChange.isTopNewKeyEqualToPrevious) {
			completionCallback.stateChangeComplete()
			return
		}
		perform(stateChange)
		completionCallback.stateChangeComplete()
	}

	private fun perform(stateChange: StateChange) {
		with(fragmentManager.beginTransaction()) {
			setAnimations(stateChange)
			handlePreviousState(stateChange)
			handleNewState(stateChange)
			commitAllowingStateLoss()
		}
	}

	private fun FragmentTransaction.setAnimations(stateChange: StateChange) {
		when (stateChange.direction) {
			StateChange.FORWARD -> {
				setCustomAnimations(
					R.anim.slide_in_right,
					R.anim.slide_out_left,
					R.anim.slide_in_right,
					R.anim.slide_out_left
				)
			}
			StateChange.BACKWARD -> {
				setCustomAnimations(
					R.anim.slide_in_left,
					R.anim.slide_out_right,
					R.anim.slide_in_left,
					R.anim.slide_out_right
				)
			}
			StateChange.REPLACE -> {
				setCustomAnimations(
					R.anim.fade_in,
					R.anim.fade_out
				)
			}
		}
	}

	private fun FragmentTransaction.handlePreviousState(stateChange: StateChange) {
		val oldStack = stateChange.getPreviousKeys<NavItem>()
		val newStack = stateChange.getNewKeys<NavItem>()
		for (oldItem in oldStack) {
			val existingFragment = fragmentManager.findFragmentByTag(oldItem.name)
			if (existingFragment != null) {
				if (oldItem !in newStack) {
					remove(existingFragment)
				} else if (!existingFragment.isDetached) {
					detach(existingFragment)
				}
			}
		}
	}

	private fun FragmentTransaction.handleNewState(stateChange: StateChange) {
		val newStack = stateChange.getNewKeys<NavItem>()
		for (newItem in newStack) {
			val existingFragment = fragmentManager.findFragmentByTag(newItem.name)
			if (newItem == stateChange.topNewKey()) {
				handleNewTop(newItem, existingFragment)
			} else if (existingFragment?.isDetached == false) {
				detach(existingFragment)
			}
		}
	}

	private fun FragmentTransaction.handleNewTop(item: NavItem, existingFragment: Fragment?) {
		val topFragment = when {
			existingFragment == null -> {
				val newFragment = createFragment(item.name)
				add(containerId, newFragment, item.name)
				newFragment
			}
			existingFragment.isRemoving -> {
				val newFragment = createFragment(item.name)
				replace(containerId, newFragment, item.name)
				newFragment
			}
			existingFragment.isDetached -> {
				attach(existingFragment)
				existingFragment
			}
			else -> existingFragment
		}
		topFragment.mergeArguments(item.arguments)
	}

	private fun Fragment.mergeArguments(newArguments: Bundle) {
		val finalArguments = arguments ?: Bundle()
		finalArguments.putAll(newArguments)
		arguments = finalArguments
	}

	private fun createFragment(name: String) =
		fragmentFactory.createFragment(name) ?: error("No fragment factory for $name")
}

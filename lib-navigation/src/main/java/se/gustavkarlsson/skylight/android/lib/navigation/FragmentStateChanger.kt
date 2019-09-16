package se.gustavkarlsson.skylight.android.lib.navigation

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.zhuinden.simplestack.StateChange
import com.zhuinden.simplestack.StateChanger

// Stolen from https://github.com/Zhuinden/simple-stack/blob/6f72e80/simple-stack-example-basic-kotlin-fragment/src/main/java/com/zhuinden/simplestackexamplekotlinfragment/core/navigation/FragmentStateChanger.kt
internal class FragmentStateChanger(
	private val fragmentManager: FragmentManager,
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
		val fragmentTransaction = fragmentManager.beginTransaction().apply {
			setAnimations(stateChange)
			handlePreviousState(stateChange)
			handleNewState(stateChange)
		}
		fragmentTransaction.commitAllowingStateLoss()
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
		}
	}

	private fun FragmentTransaction.handlePreviousState(stateChange: StateChange) {
		val oldState = stateChange.getPreviousKeys<NavItem>()
		val newState = stateChange.getNewKeys<NavItem>()
		for (oldKey in oldState) {
			val fragment = fragmentManager.findFragmentByTag(oldKey.name)
			if (fragment != null) {
				if (oldKey !in newState) {
					remove(fragment)
				} else if (!fragment.isDetached) {
					detach(fragment)
				}
			}
		}
	}

	private fun FragmentTransaction.handleNewState(stateChange: StateChange) {
		val newState = stateChange.getNewKeys<NavItem>()
		for (newKey in newState) {
			val fragment = fragmentManager.findFragmentByTag(newKey.name)
			if (newKey == stateChange.topNewKey()) {
				if (fragment != null) {
					if (fragment.isRemoving) { // Fragments are quirky, they die asynchronously. Ignore if they're still there.
						val newFragment = createFragment(newKey)
						replace(containerId, newFragment, newKey.name)
					} else if (fragment.isDetached) {
						attach(fragment)
					}
				} else {
					val newFragment = createFragment(newKey)
					add(containerId, newFragment, newKey.name)
				}
			} else {
				if (fragment != null && !fragment.isDetached) {
					detach(fragment)
				}
			}
		}
	}

	private fun createFragment(item: NavItem): Fragment {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}
}

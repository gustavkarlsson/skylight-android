package se.gustavkarlsson.skylight.android.lib.navigationsetup

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.zhuinden.simplestack.StateChange
import com.zhuinden.simplestack.StateChanger
import se.gustavkarlsson.skylight.android.entities.AnimationConfig
import se.gustavkarlsson.skylight.android.entities.Animations
import se.gustavkarlsson.skylight.android.lib.navigation.BackstackListener
import se.gustavkarlsson.skylight.android.lib.navigation.Screen
import timber.log.Timber

// Stolen from https://github.com/Zhuinden/simple-stack/blob/a9adb03/simple-stack-example-basic-kotlin-fragment/src/main/java/com/zhuinden/simplestackexamplekotlinfragment/core/navigation/FragmentStateChanger.kt
internal class FragmentStateChanger(
    private val fragmentManager: FragmentManager,
    private val containerId: Int,
    private val animationConfig: AnimationConfig,
    private val backstackListeners: List<BackstackListener>
) : StateChanger {
    override fun handleStateChange(
        stateChange: StateChange,
        completionCallback: StateChanger.Callback
    ) {
        if (!stateChange.isTopNewKeyEqualToPrevious) perform(stateChange)
        notifyListeners(stateChange)
        log(stateChange)
        completionCallback.stateChangeComplete()
    }

    private fun notifyListeners(stateChange: StateChange) {
        val old = stateChange.getPreviousKeys<Screen>()
        val new = stateChange.getNewKeys<Screen>()
        backstackListeners.forEach { it.onBackstackChanged(old, new) }
    }

    private fun perform(stateChange: StateChange) {
        fragmentManager.executePendingTransactions()
        with(fragmentManager.beginTransaction()) {
            setAnimations(stateChange)
            cleanUpPreviousState(stateChange)
            applyNewState(stateChange)
            commitAllowingStateLoss()
        }
    }

    private fun FragmentTransaction.setAnimations(stateChange: StateChange) {
        val animations = when (stateChange.direction) {
            StateChange.FORWARD -> animationConfig.forward
            StateChange.BACKWARD -> animationConfig.backward
            StateChange.REPLACE -> animationConfig.replace
            else -> error("Unsupported direction: ${stateChange.direction}")
        }
        setAnimations(animations)
    }

    private fun FragmentTransaction.cleanUpPreviousState(stateChange: StateChange) {
        val oldStack = stateChange.getPreviousKeys<Screen>()
        val newStack = stateChange.getNewKeys<Screen>()
        for (oldScreen in oldStack) {
            val existingFragment = fragmentManager.findFragmentByTag(oldScreen.tag)
            if (existingFragment != null) {
                if (oldScreen !in newStack) {
                    remove(existingFragment)
                } else if (!existingFragment.isDetached) {
                    detach(existingFragment)
                }
            }
        }
    }

    private fun FragmentTransaction.applyNewState(stateChange: StateChange) {
        val newStack = stateChange.getNewKeys<Screen>()
        for (newScreen in newStack) {
            val existingFragment = fragmentManager.findFragmentByTag(newScreen.tag)
            if (newScreen == stateChange.topNewKey()) {
                setNewTop(newScreen, existingFragment)
            } else if (existingFragment?.isDetached == false) {
                detach(existingFragment)
            }
        }
    }

    private fun FragmentTransaction.setNewTop(screen: Screen, existingFragment: Fragment?) {
        when {
            existingFragment == null -> {
                val newFragment = screen.createFragment()
                add(containerId, newFragment, screen.tag)
            }
            existingFragment.isRemoving -> {
                val newFragment = screen.createFragment()
                replace(containerId, newFragment, screen.tag)
            }
            existingFragment.isDetached -> attach(existingFragment)
        }
    }
}

private fun log(stateChange: StateChange) {
    val direction = when (stateChange.direction) {
        StateChange.FORWARD -> "forward"
        StateChange.BACKWARD -> "backward"
        else -> "replace"
    }
    Timber.i("Backstack set to ${stateChange.getNewKeys<Any>()} with direction: $direction")
}

private fun FragmentTransaction.setAnimations(animations: Animations) {
    setCustomAnimations(
        animations.enter,
        animations.exit,
        animations.popEnter,
        animations.popExit
    )
}

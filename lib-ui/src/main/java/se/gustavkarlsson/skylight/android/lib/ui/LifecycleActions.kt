package se.gustavkarlsson.skylight.android.lib.ui

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Lifecycle.Event.ON_ANY
import androidx.lifecycle.Lifecycle.Event.ON_CREATE
import androidx.lifecycle.Lifecycle.Event.ON_DESTROY
import androidx.lifecycle.Lifecycle.Event.ON_PAUSE
import androidx.lifecycle.Lifecycle.Event.ON_RESUME
import androidx.lifecycle.Lifecycle.Event.ON_START
import androidx.lifecycle.Lifecycle.Event.ON_STOP
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent


abstract class LifecycleAction(
	action: () -> Unit,
	lifecycleToRemoveFrom: Lifecycle?
) : LifecycleObserver {
	protected var action =
		if (lifecycleToRemoveFrom == null) {
			action
		} else {
			{
				action()
				lifecycleToRemoveFrom.removeObserver(this)
			}
		}
}

class OnCreate(
	action: () -> Unit,
	lifecycleToRemoveFrom: Lifecycle? = null
) : LifecycleAction(action, lifecycleToRemoveFrom), LifecycleObserver {
	@OnLifecycleEvent(ON_CREATE)
	fun invoke() = action()
}

class OnStart(
	action: () -> Unit,
	lifecycleToRemoveFrom: Lifecycle? = null
) : LifecycleAction(action, lifecycleToRemoveFrom), LifecycleObserver {
	@OnLifecycleEvent(ON_START)
	fun invoke() = action()
}

class OnResume(
	action: () -> Unit,
	lifecycleToRemoveFrom: Lifecycle? = null
) : LifecycleAction(action, lifecycleToRemoveFrom), LifecycleObserver {
	@OnLifecycleEvent(ON_RESUME)
	fun invoke() = action()
}

class OnPause(
	action: () -> Unit,
	lifecycleToRemoveFrom: Lifecycle? = null
) : LifecycleAction(action, lifecycleToRemoveFrom), LifecycleObserver {
	@OnLifecycleEvent(ON_PAUSE)
	fun invoke() = action()
}

class OnStop(
	action: () -> Unit,
	lifecycleToRemoveFrom: Lifecycle? = null
) : LifecycleAction(action, lifecycleToRemoveFrom), LifecycleObserver {
	@OnLifecycleEvent(ON_STOP)
	fun invoke() = action()
}

class OnDestroy(
	action: () -> Unit,
	lifecycleToRemoveFrom: Lifecycle? = null
) : LifecycleAction(action, lifecycleToRemoveFrom), LifecycleObserver {
	@OnLifecycleEvent(ON_DESTROY)
	fun invoke() = action()
}

class OnAny(
	action: () -> Unit,
	lifecycleToRemoveFrom: Lifecycle? = null
) : LifecycleAction(action, lifecycleToRemoveFrom), LifecycleObserver {
	@OnLifecycleEvent(ON_ANY)
	fun invoke() = action()
}

fun doOnEvery(owner: LifecycleOwner, event: Lifecycle.Event, block: () -> Unit): LifecycleAction {
	val onAction = when (event) {
		ON_CREATE -> OnCreate(block)
		ON_START -> OnStart(block)
		ON_RESUME -> OnResume(block)
		ON_PAUSE -> OnPause(block)
		ON_STOP -> OnStop(block)
		ON_DESTROY -> OnDestroy(block)
		ON_ANY -> OnAny(block)
	}
	owner.lifecycle.addObserver(onAction)
	return onAction
}

fun <T> T.doOnEvery(owner: LifecycleOwner, event: Lifecycle.Event, block: (T) -> Unit): LifecycleAction =
	doOnEvery(owner, event) { -> block(this) }

fun doOnNext(owner: LifecycleOwner, event: Lifecycle.Event, block: () -> Unit) {
	val lifecycle = owner.lifecycle
	val onAction = when (event) {
		ON_CREATE -> OnCreate(block, lifecycle)
		ON_START -> OnStart(block, lifecycle)
		ON_RESUME -> OnResume(block, lifecycle)
		ON_PAUSE -> OnPause(block, lifecycle)
		ON_STOP -> OnStop(block, lifecycle)
		ON_DESTROY -> OnDestroy(block, lifecycle)
		ON_ANY -> OnAny(block, lifecycle)
	}
	lifecycle.addObserver(onAction)
}

fun <T> T.doOnNext(owner: LifecycleOwner, event: Lifecycle.Event, block: (T) -> Unit) =
	doOnNext(owner, event) { -> block(this) }

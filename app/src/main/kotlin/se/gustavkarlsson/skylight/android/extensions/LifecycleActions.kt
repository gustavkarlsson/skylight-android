package se.gustavkarlsson.skylight.android.extensions

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.Lifecycle.Event.*
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.OnLifecycleEvent

private abstract class OnAction(
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

private class OnCreate(
	action: () -> Unit,
	lifecycleToRemoveFrom: Lifecycle? = null
) : OnAction(action, lifecycleToRemoveFrom), LifecycleObserver {
	@OnLifecycleEvent(ON_CREATE)
	fun invoke() = action()
}

private class OnStart(
	action: () -> Unit,
	lifecycleToRemoveFrom: Lifecycle? = null
) : OnAction(action, lifecycleToRemoveFrom), LifecycleObserver {
	@OnLifecycleEvent(ON_START)
	fun invoke() = action()
}

private class OnResume(
	action: () -> Unit,
	lifecycleToRemoveFrom: Lifecycle? = null
) : OnAction(action, lifecycleToRemoveFrom), LifecycleObserver {
	@OnLifecycleEvent(ON_RESUME)
	fun invoke() = action()
}

private class OnPause(
	action: () -> Unit,
	lifecycleToRemoveFrom: Lifecycle? = null
) : OnAction(action, lifecycleToRemoveFrom), LifecycleObserver {
	@OnLifecycleEvent(ON_PAUSE)
	fun invoke() = action()
}

private class OnStop(
	action: () -> Unit,
	lifecycleToRemoveFrom: Lifecycle? = null
) : OnAction(action, lifecycleToRemoveFrom), LifecycleObserver {
	@OnLifecycleEvent(ON_STOP)
	fun invoke() = action()
}

private class OnDestroy(
	action: () -> Unit,
	lifecycleToRemoveFrom: Lifecycle? = null
) : OnAction(action, lifecycleToRemoveFrom), LifecycleObserver {
	@OnLifecycleEvent(ON_DESTROY)
	fun invoke() = action()
}

private class OnAny(
	action: () -> Unit,
	lifecycleToRemoveFrom: Lifecycle? = null
) : OnAction(action, lifecycleToRemoveFrom), LifecycleObserver {
	@OnLifecycleEvent(ON_ANY)
	fun invoke() = action()
}

fun doOn(owner: LifecycleOwner, event: Lifecycle.Event, block: () -> Unit) {
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
}

fun <T> T.doOn(owner: LifecycleOwner, event: Lifecycle.Event, block: (T) -> Unit) =
	se.gustavkarlsson.skylight.android.extensions.doOn(owner, event) { block(this) }

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
	se.gustavkarlsson.skylight.android.extensions.doOnNext(owner, event) { block(this) }

package se.gustavkarlsson.skylight.android.extensions

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.Lifecycle.Event.*
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.OnLifecycleEvent

private class OnCreate(private val action: () -> Unit) : LifecycleObserver {

	@OnLifecycleEvent(ON_CREATE)
	fun invoke() = action()
}

private class OnStart(private val action: () -> Unit) : LifecycleObserver {

	@OnLifecycleEvent(ON_START)
	fun invoke() = action()
}

private class OnResume(private val action: () -> Unit) : LifecycleObserver {

	@OnLifecycleEvent(ON_RESUME)
	fun invoke() = action()
}

private class OnPause(private val action: () -> Unit) : LifecycleObserver {

	@OnLifecycleEvent(ON_PAUSE)
	fun invoke() = action()
}

private class OnStop(private val action: () -> Unit) : LifecycleObserver {

	@OnLifecycleEvent(ON_STOP)
	fun invoke() = action()
}

private class OnDestroy(private val action: () -> Unit) : LifecycleObserver {

	@OnLifecycleEvent(ON_DESTROY)
	fun invoke() = action()
}

private class OnAny(private val action: () -> Unit) : LifecycleObserver {

	@OnLifecycleEvent(ON_ANY)
	fun invoke() = action()
}

fun doOnLifecycle(owner: LifecycleOwner, event: Lifecycle.Event, block: () -> Unit) {
	when (event) {
		ON_CREATE -> owner.lifecycle.addObserver(OnCreate(block))
		ON_START -> owner.lifecycle.addObserver(OnStart(block))
		ON_RESUME -> owner.lifecycle.addObserver(OnResume(block))
		ON_PAUSE -> owner.lifecycle.addObserver(OnPause(block))
		ON_STOP -> owner.lifecycle.addObserver(OnStop(block))
		ON_DESTROY -> owner.lifecycle.addObserver(OnDestroy(block))
		ON_ANY -> owner.lifecycle.addObserver(OnAny(block))
	}
}

fun <T> T.doOnLifecycle(owner: LifecycleOwner, event: Lifecycle.Event, block: (T) -> Unit) =
	se.gustavkarlsson.skylight.android.extensions.doOnLifecycle(owner, event) { block(this) }

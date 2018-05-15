package se.gustavkarlsson.skylight.android.gui

import android.support.v7.app.AppCompatActivity

abstract class LifecycleSchedulingActivity : AppCompatActivity() {

	private val onPauseTasks = mutableListOf<() -> Unit>()
	private val onStopTasks = mutableListOf<() -> Unit>()
	private val onDestroyTasks = mutableListOf<() -> Unit>()

	final override fun onPause() {
		onPauseTasks.forEach { it() }
		onPauseTasks.clear()
		super.onPause()
	}

	final override fun onStop() {
		onStopTasks.forEach { it() }
		onStopTasks.clear()
		super.onStop()
	}

	final override fun onDestroy() {
		onDestroyTasks.forEach { it() }
		onDestroyTasks.clear()
		super.onDestroy()
	}

	protected fun <T> T.doOnPause(block: (T) -> Unit): T {
		onPauseTasks.add { block(this) }
		return this
	}

	protected fun <T> T.doOnStop(block: (T) -> Unit): T {
		onStopTasks.add { block(this) }
		return this
	}

	protected fun <T> T.doOnDestroy(block: (T) -> Unit): T {
		onDestroyTasks.add { block(this) }
		return this
	}
}

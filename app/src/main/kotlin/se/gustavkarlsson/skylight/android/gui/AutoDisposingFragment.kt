package se.gustavkarlsson.skylight.android.gui

import android.support.v4.app.Fragment
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

abstract class AutoDisposingFragment : Fragment() {

	private val onPauseDisposables = CompositeDisposable()
	private val onStopDisposables = CompositeDisposable()
	private val onDestroyDisposables = CompositeDisposable()

	final override fun onPause() {
		onPauseDisposables.clear()
		super.onPause()
	}

	final override fun onStop() {
		onStopDisposables.clear()
		super.onStop()
	}

	final override fun onDestroy() {
		onDestroyDisposables.clear()
		super.onDestroy()
	}

	protected fun Disposable.autoDisposeOnPause() {
		onPauseDisposables.add(this)
	}

	protected fun Disposable.autoDisposeOnStop() {
		onStopDisposables.add(this)
	}

	protected fun Disposable.autoDisposeOnDestroy() {
		onDestroyDisposables.add(this)
	}
}

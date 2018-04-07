package se.gustavkarlsson.skylight.android.gui

import android.support.v7.app.AppCompatActivity
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import se.gustavkarlsson.skylight.android.extensions.addTo

abstract class AutoDisposingActivity : AppCompatActivity() {

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
		this.addTo(onPauseDisposables)
	}

	protected fun Disposable.autoDisposeOnStop() {
		this.addTo(onStopDisposables)
	}

	protected fun Disposable.autoDisposeOnDestroy() {
		this.addTo(onDestroyDisposables)
	}
}

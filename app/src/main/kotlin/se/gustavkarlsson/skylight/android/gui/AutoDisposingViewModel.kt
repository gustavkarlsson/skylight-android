package se.gustavkarlsson.skylight.android.gui

import android.arch.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

abstract class AutoDisposingViewModel : ViewModel() {

	private val disposables = CompositeDisposable()

	final override fun onCleared() {
		disposables.clear()
	}

	protected fun Disposable.autoDisposeOnCleared() {
		disposables.add(this)
	}
}

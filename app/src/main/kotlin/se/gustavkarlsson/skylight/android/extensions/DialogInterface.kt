package se.gustavkarlsson.skylight.android.extensions

import android.content.DialogInterface
import io.reactivex.disposables.Disposable

fun DialogInterface.toDisposable(): Disposable = object : Disposable {
	private var disposed = false
	override fun isDisposed(): Boolean = disposed

	override fun dispose() {
		cancel()
		disposed = true
	}

}

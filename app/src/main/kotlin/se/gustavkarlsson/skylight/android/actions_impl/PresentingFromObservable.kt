package se.gustavkarlsson.skylight.android.actions_impl

import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import se.gustavkarlsson.skylight.android.services.Presenter

abstract class PresentingFromObservable<T>(
	private val observable: Observable<T>,
	private val presenter: Presenter<T>
) {

	private var disposable: Disposable? = null

	@Synchronized fun start() {
		require(!started) { "Already started" }
		disposable = observable.subscribe {
			presenter.present(it)
		}
	}

	@Synchronized fun stop() {
		require(started) { "Not started" }
		disposable!!.dispose()
		disposable = null
	}

	private val started: Boolean
		get() = disposable != null
}

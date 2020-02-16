package se.gustavkarlsson.skylight.android.lib.ui.extensions

import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable

fun Completable.bind(fragment: Fragment) = toObservable<Unit>()
	.bind(fragment) {}

fun <T : Any> Maybe<T>.bind(fragment: Fragment, block: (value: T) -> Unit) = toObservable()
	.bind(fragment, block)

fun <T : Any> Single<T>.bind(fragment: Fragment, block: (value: T) -> Unit) = toObservable()
	.bind(fragment, block)

fun <T : Any> Observable<T>.bind(fragment: Fragment, block: (value: T) -> Unit) =
	bind(fragment.viewLifecycleOwner.lifecycle, javaClass.simpleName, block)

private fun <T : Any> Observable<T>.bind(
	lifecycle: Lifecycle,
	sourceName: String,
	block: (value: T) -> Unit
) = lifecycle.addObserver(
	ObservableBinding(
		this,
		block,
		sourceName,
		AndroidSchedulers.mainThread()
	)
)

private class ObservableBinding<T>(
	private val observable: Observable<T>,
	private val block: (value: T) -> Unit,
	private val sourceName: String,
	private val scheduler: Scheduler
) : LifecycleEventObserver, Observer<T> {

	private var disposable: Disposable? = null

	override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
		@Suppress("NON_EXHAUSTIVE_WHEN")
		when (event) {
			Lifecycle.Event.ON_START -> {
				observable
					.observeOn(scheduler)
					.subscribe(this)
			}
			Lifecycle.Event.ON_STOP -> {
				synchronized(this) {
					disposable?.dispose()
					disposable = null
				}
			}
		}
	}

	override fun onSubscribe(disposable: Disposable) =
		synchronized(this) { this.disposable = disposable }

	override fun onNext(value: T) = block(value)

	override fun onComplete() = synchronized(this) { disposable = null }

	override fun onError(throwable: Throwable) = throw BindingException(sourceName, throwable)
}

private class BindingException(source: String, cause: Throwable) :
	Exception("Binding in '$source' terminated with error", cause)

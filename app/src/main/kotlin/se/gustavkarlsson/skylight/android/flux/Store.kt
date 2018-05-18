package se.gustavkarlsson.skylight.android.flux

import com.jakewharton.rxrelay2.PublishRelay
import com.jakewharton.rxrelay2.Relay
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable

open class Store<S, A, R>(
	initialState: S,
	transformers: Iterable<ObservableTransformer<A, R>>,
	accumulator: (current: S, result: R) -> S,
	private vararg val startActions: A
) {

	private var statesSubscription: Disposable? = null

	fun postAction(action: A) {
		if (statesSubscription == null) {
			throw IllegalStateException("Can't accept actions until started")
		} else {
			actions.accept(action)
		}
	}

	private val actions: Relay<A> = PublishRelay.create<A>()

	private val results: Observable<R> = actions
		.publish { actions ->
			val results = transformers
				.map { actions.compose(it) }
			Observable.merge(results)
		}

	private val connectableStates = results
		.scan(initialState, accumulator)
		.replay(1)

	val states: Observable<S> = connectableStates
		.observeOn(AndroidSchedulers.mainThread())

	@Synchronized
	fun start() {
		if (statesSubscription != null) return
		statesSubscription = states.subscribe()
		connectableStates.connect()
		startActions.forEach(::postAction)
	}
}

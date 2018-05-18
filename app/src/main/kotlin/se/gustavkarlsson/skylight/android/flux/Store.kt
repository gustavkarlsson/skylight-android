package se.gustavkarlsson.skylight.android.flux

import com.jakewharton.rxrelay2.PublishRelay
import com.jakewharton.rxrelay2.Relay
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.disposables.Disposable

open class Store<S : Any, A : Any, R : Any>(
	initialState: S,
	transformers: Iterable<ObservableTransformer<A, R>>,
	reducer: (current: S, result: R) -> S
) {

	private var statesSubscription: Disposable? = null

	fun postAction(action: A) {
		if (statesSubscription == null) {
			throw IllegalStateException("Can't accept actions until started")
		}
		actions.accept(action)
	}

	private val actions: Relay<A> = PublishRelay.create<A>()

	private val results: Observable<R> = actions
		.publish { actions ->
			val results = transformers
				.map { actions.compose(it) }
			Observable.merge(results)
		}

	private val connectableStates = results
		.scan(initialState, reducer)
		.replay(1)

	val states: Observable<S>
		@Synchronized
		get() {
			if (statesSubscription == null) {
				throw IllegalStateException("Can't observe state until started")
			}
			return connectableStates
		}

	@Synchronized
	fun start() {
		if (statesSubscription != null) return
		statesSubscription = connectableStates.subscribe()
		connectableStates.connect()
	}
}

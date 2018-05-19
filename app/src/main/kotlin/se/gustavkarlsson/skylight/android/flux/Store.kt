package se.gustavkarlsson.skylight.android.flux

import com.jakewharton.rxrelay2.PublishRelay
import com.jakewharton.rxrelay2.Relay
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.ofType

class Store<State : Any, Action : Any, Result : Any>
internal constructor(
	initialState: State,
	transformers: List<(Observable<Action>) -> Observable<Result>>,
	reducer: (current: State, result: Result) -> State,
	observeScheduler: Scheduler?,
	private val startActions: List<Action>
) {
	private var statesSubscription: Disposable? = null

	fun postAction(action: Action) {
		if (statesSubscription == null) {
			throw IllegalStateException("Can't post actions until started")
		}
		actions.accept(action)
	}

	private val actions: Relay<Action> = PublishRelay.create<Action>()

	private val results: Observable<Result> = actions
		.publish { actions ->
			val results = transformers
				.map { actions.compose(it) }
			Observable.merge(results)
		}

	private val connectableStates = results
		.scan(initialState, reducer)
		.run {
			if (observeScheduler != null) {
				observeOn(observeScheduler)
			} else {
				this
			}
		}
		.replay(1)

	@Synchronized
	fun getState(): Observable<State> {
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
		startActions.forEach(::postAction)
	}
}

class StoreBuilder<State : Any, Action : Any, Result : Any>
internal constructor(
	private val initialState: State,
	private val reducer: (current: State, result: Result) -> State
) {
	private val transformers: MutableList<(Observable<Action>) -> Observable<Result>> =
		mutableListOf()
	private val startActions: MutableList<Action> = mutableListOf()
	private var observeScheduler: Scheduler? = null

	fun addActionTransformer(transformer: (Observable<Action>) -> Observable<Result>) {
		transformers += transformer
	}

	@JvmName("addActionTransformer6")
	inline fun <reified A : Action, reified R : Result> flatMapAction(noinline mapper: (A) -> Observable<R>) {
		addActionTransformer { actions: Observable<Action> ->
			actions.ofType<A>().flatMap(mapper)
		}
	}

	@JvmName("addActionTransformer7")
	inline fun <reified A : Action, reified R : Result> switchMapAction(noinline mapper: (A) -> Observable<R>) {
		addActionTransformer { actions: Observable<Action> ->
			actions.ofType<A>().switchMap(mapper)
		}
	}

	@JvmName("addActionTransformer8")
	inline fun <reified A : Action, reified R : Result> mapAction(noinline mapper: (A) -> R) {
		addActionTransformer { actions: Observable<Action> ->
			actions.ofType<A>().map(mapper)
		}
	}

	fun addStartActions(vararg actions: Action) {
		startActions += actions
	}

	fun setObserveScheduler(scheduler: Scheduler?) {
		observeScheduler = scheduler
	}

	fun build(): Store<State, Action, Result> {
		if (transformers.isEmpty()) throw IllegalStateException("No transformers added")
		return Store(initialState, transformers, reducer, observeScheduler, startActions)
	}
}

fun <State : Any, Action : Any, Result : Any> buildStore(
	initialState: State,
	reducer: (current: State, result: Result) -> State,
	block: StoreBuilder<State, Action, Result>.() -> Unit
): Store<State, Action, Result> {
	val builder = StoreBuilder<State, Action, Result>(initialState, reducer)
	builder.block()
	return builder.build()
}

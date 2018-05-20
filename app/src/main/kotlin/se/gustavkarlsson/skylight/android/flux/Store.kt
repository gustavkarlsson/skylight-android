package se.gustavkarlsson.skylight.android.flux

import com.jakewharton.rxrelay2.PublishRelay
import com.jakewharton.rxrelay2.Relay
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import io.reactivex.rxkotlin.ofType
import io.reactivex.schedulers.Schedulers

class Store<State : Any, Action : Any, Result : Any>
internal constructor(
	initialState: State,
	reducer: (current: State, result: Result) -> State,
	actionTransformers: List<(Observable<Action>) -> Observable<Result>>,
	actionWithStateTransformers: List<(Observable<State>, Observable<Action>) -> Observable<Result>>,
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
			val actionResults =
				actionTransformers.map { it(actions) }
			val actionWithStateResults =
				actionWithStateTransformers.map { it(connectableStates, actions) }
			Observable.merge(actionResults + actionWithStateResults)
		}

	private val connectableStates = results
		.observeOn(Schedulers.newThread()) // Scan is not thread safe so must run sequentially
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
	private val actionTransformers:
		MutableList<(Observable<Action>) -> Observable<Result>> = mutableListOf()
	private val actionWithStateTransformers:
		MutableList<(Observable<State>, Observable<Action>) -> Observable<Result>> = mutableListOf()
	private var observeScheduler: Scheduler? = null
	private val startActions: MutableList<Action> = mutableListOf()

	fun transformActions(vararg transformers: (Observable<Action>) -> Observable<Result>) {
		actionTransformers += transformers
	}

	inline fun <reified A : Action, reified R : Result> mapAction(noinline mapper: (A) -> R) {
		transformActions({ actions: Observable<Action> ->
			actions.ofType<A>().map(mapper)
		})
	}

	inline fun <reified A : Action, reified R : Result> flatMapAction(
		noinline mapper: (A) -> Observable<R>
	) {
		transformActions({ actions: Observable<Action> ->
			actions.ofType<A>().flatMap(mapper)
		})
	}

	inline fun <reified A : Action, reified R : Result> switchMapAction(
		noinline mapper: (A) -> Observable<R>
	) {
		transformActions({ actions: Observable<Action> ->
			actions.ofType<A>().switchMap(mapper)
		})
	}

	fun transformActionsWithState(vararg transformers: (Observable<State>, Observable<Action>) -> Observable<Result>) {
		actionWithStateTransformers += transformers
	}

	inline fun <reified A : Action, reified R : Result> mapActionWithState(noinline mapper: (State, A) -> R) {
		val function = BiFunction<A, State, R> { action, state ->
			mapper(state, action)
		}
		transformActionsWithState({ states: Observable<State>, actions: Observable<Action> ->
			actions.ofType<A>().withLatestFrom(states, function)
		})
	}

	inline fun <reified A : Action, reified R : Result> flatMapActionWithState(noinline mapper: (State, A) -> Observable<R>) {
		val function = BiFunction<A, State, Observable<R>> { action, state ->
			mapper(state, action)
		}
		transformActionsWithState({ states: Observable<State>, actions: Observable<Action> ->
			actions.ofType<A>().withLatestFrom(states, function).flatMap { it }
		})
	}

	inline fun <reified A : Action, reified R : Result> switchMapActionWithState(noinline mapper: (State, A) -> Observable<R>) {
		val function = BiFunction<A, State, Observable<R>> { action, state ->
			mapper(state, action)
		}
		transformActionsWithState({ states: Observable<State>, actions: Observable<Action> ->
			actions.ofType<A>().withLatestFrom(states, function).switchMap { it }
		})
	}

	fun setObserveScheduler(scheduler: Scheduler?) {
		observeScheduler = scheduler
	}

	fun addStartActions(vararg actions: Action) {
		startActions += actions
	}

	fun build(): Store<State, Action, Result> {
		if (actionTransformers.isEmpty()) throw IllegalStateException("No action transformers added")
		return Store(
			initialState,
			reducer,
			actionTransformers,
			actionWithStateTransformers,
			observeScheduler,
			startActions
		)
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

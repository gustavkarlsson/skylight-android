package se.gustavkarlsson.flux

import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.rxkotlin.ofType
import io.reactivex.rxkotlin.withLatestFrom
import io.reactivex.schedulers.Schedulers

class StoreBuilder<State : Any, Action : Any, Result : Any>
internal constructor() {
	private var initialState: (() -> State)? = null
	private val actionTransformers =
		mutableListOf<ActionTransformer<Action, Result>>()
	private val actionWithStateTransformers =
		mutableListOf<ActionWithStateTransformer<State, Action, Result>>()
	private val resultReducers =
		mutableListOf<ResultReducer<State, Result>>()
	private val stateWatchers =
		mutableListOf<StateWatcher<State>>()
	private var observeScheduler: Scheduler? = null

	fun initWith(initialState: () -> State) {
		this.initialState = initialState
	}

	fun initWith(initialState: State) {
		initWith { initialState }
	}

	fun transformActions(
		transformer: (Observable<Action>) -> Observable<Result>
	) {
		actionTransformers += transformer
	}

	inline fun <reified A : Action, R : Result> mapAction(
		noinline mapper: (A) -> R
	) {
		transformActions { actions: Observable<Action> ->
			actions.ofType<A>().map(mapper)
		}
	}

	inline fun <reified A : Action, R : Result> flatMapAction(
		noinline mapper: (A) -> Observable<R>
	) {
		transformActions { actions: Observable<Action> ->
			actions.ofType<A>().flatMap(mapper)
		}
	}

	inline fun <reified A : Action, R : Result> switchMapAction(
		noinline mapper: (A) -> Observable<R>
	) {
		transformActions { actions: Observable<Action> ->
			actions.ofType<A>().switchMap(mapper)
		}
	}

	fun transformActionsWithState(
		transformers: (Observable<State>, Observable<Action>) -> Observable<Result>
	) {
		actionWithStateTransformers += transformers
	}

	inline fun <reified A : Action, R : Result> mapActionWithState(
		noinline mapper: (A, State) -> R
	) {
		transformActionsWithState { states: Observable<State>, actions: Observable<Action> ->
			actions.ofType<A>().withLatestFrom(states, mapper)
		}
	}

	inline fun <reified A : Action, R : Result> flatMapActionWithState(
		noinline mapper: (A, State) -> Observable<R>
	) {
		transformActionsWithState { states: Observable<State>, actions: Observable<Action> ->
			actions.ofType<A>().withLatestFrom(states, mapper).flatMap { it }
		}
	}

	inline fun <reified A : Action, R : Result> switchMapActionWithState(
		noinline mapper: (A, State) -> Observable<R>
	) {
		transformActionsWithState { states: Observable<State>, actions: Observable<Action> ->
			actions.ofType<A>().withLatestFrom(states, mapper).switchMap { it }
		}
	}

	fun <R : Result> reduceResult(clazz: Class<R>, reducer: (State, R) -> State) {
		@Suppress("UNCHECKED_CAST")
		resultReducers += ResultReducer<State, Result>(
			clazz,
			reducer as (State, Result) -> State
		)
	}

	inline fun <reified R : Result> reduceResult(noinline reducer: (State, R) -> State) {
		reduceResult(R::class.java, reducer)
	}

	fun observeOn(scheduler: Scheduler?) {
		observeScheduler = scheduler
	}

	inline fun <reified A : Action> doOnAction(crossinline block: (A) -> Unit) {
		flatMapAction { action: A ->
			block(action)
			Observable.empty<Result>()
		}
	}

	inline fun <reified R : Result> doOnResult(crossinline block: (R) -> Unit) {
		reduceResult { state: State, result: R ->
			block(result)
			state
		}
	}

	fun doOnState(block: (State) -> Unit) {
		stateWatchers += block
	}

	fun build(): Store<State, Action, Result> {
		val initialState = initialState
			?: throw IllegalStateException("No initial state set")
		if (actionTransformers.isEmpty() && actionWithStateTransformers.isEmpty()) {
			throw IllegalStateException("No action transformers defined")
		}
		if (resultReducers.isEmpty()) throw IllegalStateException("No result reducers defined")
		return Store(
			initialState,
			actionTransformers,
			actionWithStateTransformers,
			resultReducers,
			stateWatchers,
			observeScheduler,
			Schedulers.newThread() // Scan is not thread safe so a dedicated thread is a good idea
		)
	}
}

fun <State : Any, Action : Any, Result : Any> buildStore(
	block: StoreBuilder<State, Action, Result>.() -> Unit
): Store<State, Action, Result> {
	val builder = StoreBuilder<State, Action, Result>()
	builder.block()
	return builder.build()
}

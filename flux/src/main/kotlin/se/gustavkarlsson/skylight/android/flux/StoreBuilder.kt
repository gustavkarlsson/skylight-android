package se.gustavkarlsson.skylight.android.flux

import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.functions.BiFunction
import io.reactivex.rxkotlin.ofType

class StoreBuilder<State : Any, Action : Any, Result : Any>
internal constructor() {
	private var initialState: State? = null
	private val actionTransformers:
		MutableList<(Observable<Action>) -> Observable<Result>> = mutableListOf()
	private val actionWithStateTransformers:
		MutableList<(Observable<State>, Observable<Action>) -> Observable<Result>> = mutableListOf()
	private val reducers = linkedMapOf<Class<out Result>, (State, Result) -> State>()
	private var observeScheduler: Scheduler? = null
	private val startActions: MutableList<Action> = mutableListOf()

	fun setInitialState(state: State) {
		initialState = state
	}

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

	fun <R : Result> reduce(clazz: Class<R>, reducer: (State, R) -> State) {
		@Suppress("UNCHECKED_CAST")
		reducers[clazz] = (reducer as (State, Result) -> State)
	}

	inline fun <reified R : Result> reduce(noinline reducer: (State, R) -> State) {
		reduce(R::class.java, reducer)
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
		val initialState = initialState ?: throw IllegalStateException("No initial state set")
		return Store(
			initialState,
			actionTransformers,
			actionWithStateTransformers,
			reducers,
			observeScheduler,
			startActions
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
